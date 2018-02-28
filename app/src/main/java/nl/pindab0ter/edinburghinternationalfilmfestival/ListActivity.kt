package nl.pindab0ter.edinburghinternationalfilmfestival

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_master.*
import kotlinx.android.synthetic.main.film_list.*
import nl.pindab0ter.edinburghinternationalfilmfestival.R.layout.activity_master
import nl.pindab0ter.edinburghinternationalfilmfestival.R.menu.menu_list_activity
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDbHelper
import nl.pindab0ter.edinburghinternationalfilmfestival.data.network.FilmEventFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

class ListActivity : AppCompatActivity() {

    private val logTag = ListActivity::class.simpleName
    private val twoPane: Boolean // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
        get() = detail_container != null
    private lateinit var adapter: FilmEventRecyclerViewAdapter
    private var genres: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableDebugMode()

        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        setContentView(activity_master)

        setSupportActionBar(toolbar)
        toolbar.title = title

        adapter = FilmEventRecyclerViewAdapter(this, twoPane)
        film_list.adapter = adapter

        GetFilmEventsFromDatabaseTask().execute()
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(menu_list_activity, menu)

        if (genres == null) return true

        menu?.findItem(R.id.filter_placeholder)?.isVisible = false
        menu?.findItem(R.id.filter_none)?.isVisible = true

        menu?.findItem(R.id.filter)?.subMenu?.apply {
            genres!!.forEachIndexed { index, genre ->
                add(R.id.filter_group, index, index, genre)
            }
            setGroupCheckable(R.id.filter_group, true, true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.groupId) {
            R.id.sort_group -> {
                adapter.sortBy(item.itemId)
                item.isChecked = true
            }
            R.id.filter_group -> {
                if (item.itemId == R.id.filter_placeholder) return false
                adapter.filterBy(item.title)
                item.isChecked = true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun fetchFilmEvents(view: View) {
        GetFilmEventsFromDatabaseTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetFilmEventsFromDatabaseTask : AsyncTask<Unit, Unit, List<FilmEvent>>() {
        override fun doInBackground(vararg params: Unit?): List<FilmEvent>? {
            return FilmEventDAO(this@ListActivity).getAll()
        }

        override fun onPostExecute(filmEvents: List<FilmEvent>) {
            if (filmEvents.isNotEmpty()) {
                populateList(filmEvents)
            } else {
                FilmEventFetcher(this@ListActivity, { fetchedFilmEvents ->
                    populateList(fetchedFilmEvents)
                    InsertFilmEventsIntoDatabaseTask().execute(fetchedFilmEvents)
                }, { volleyError ->
                    Log.e(logTag, "$volleyError")
                    showRetry()
                }).fetch()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class InsertFilmEventsIntoDatabaseTask : AsyncTask<List<FilmEvent>, Unit, Unit>() {
        override fun doInBackground(vararg params: List<FilmEvent>?) {
            FilmEventDAO(this@ListActivity).insert(params.first().orEmpty())
        }
    }

    private fun populateList(filmEvents: List<FilmEvent>) {
        if (filmEvents.isNotEmpty()) {
            adapter.swapFilmEvents(filmEvents)
            genres = filmEvents.mapNotNull { it.genreTags?.asIterable() }.flatten().distinct().sorted()
            invalidateOptionsMenu()

            film_list.visibility = View.VISIBLE
            failed_to_load_events.visibility = View.GONE

            filmEvents.forEach { filmEvent ->
                Glide.with(this@ListActivity)
                        .load(filmEvent.imageThumbnail)
                        .load(filmEvent.imageOriginal)
                        .preload()
            }
        } else {
            showRetry()
        }
    }

    private fun showRetry() {
        film_list.visibility = View.GONE
        failed_to_load_events.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    @SuppressLint("StaticFieldLeak")
    private fun enableDebugMode() = object : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {

            FilmEventDbHelper(this@ListActivity).apply {
                deleteDatabase(FilmEventDbHelper.DATABASE_NAME)
                close()
            }

            Glide.get(applicationContext).clearDiskCache()

            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }
    }.execute()
}
