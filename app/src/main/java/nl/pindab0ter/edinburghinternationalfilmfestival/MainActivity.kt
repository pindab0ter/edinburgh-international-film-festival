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
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.film_list.*
import kotlinx.android.synthetic.main.toolbar_activity_master.*
import nl.pindab0ter.edinburghinternationalfilmfestival.R.menu.menu_list_activity
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDbHelper
import nl.pindab0ter.edinburghinternationalfilmfestival.data.network.FilmEventFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.GetFilmEventsFromDatabaseTask
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.InsertFilmEventsIntoDatabaseTask

class MainActivity : AppCompatActivity() {

    private val logTag = MainActivity::class.simpleName
    private val twoPane: Boolean // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
        get() = detail_container != null
    private lateinit var adapter: FilmEventRecyclerViewAdapter
    private lateinit var popupMenu: PopupMenu
    private var genres: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // enableDebugMode()

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main_activity))
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }

        button_toolbar_filter.setOnClickListener {
            popupMenu.show()
        }

        adapter = FilmEventRecyclerViewAdapter(this, twoPane)
        film_list.adapter = adapter
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        fetchFilmEvents()
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menu_list_activity, menu)
        popupMenu = createFilterMenu()
        return super.onCreateOptionsMenu(menu)
    }

    private fun createFilterMenu(): PopupMenu = PopupMenu(this, findViewById(R.id.button_toolbar_filter)).apply {
        menuInflater.inflate(R.menu.menu_filter, this@apply.menu)

        with(this.menu) {
            if (genres.orEmpty().isEmpty()) return@apply

            findItem(R.id.filter_failed_to_load)?.isVisible = false
            findItem(R.id.filter_all)?.isVisible = true

            genres?.forEachIndexed { index, genre ->
                add(R.id.filter_group, index, index, genre)
            }
        }

        setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.filter_failed_to_load) return@setOnMenuItemClickListener false
            adapter.filterBy(item.title)
            button_toolbar_filter.text = item.title
            item.isChecked = true
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.groupId) {
        R.id.home -> {
            // TODO: Show navigation drawer
            true
        }
        R.id.sort_group -> {
            adapter.sortBy(item.itemId)
            item.isChecked = true
            true
        }
        else -> false
    }

    fun fetchFilmEvents(view: View? = null) {
        GetFilmEventsFromDatabaseTask(this, { filmEvents ->
            if (filmEvents.isNotEmpty()) populateList(filmEvents)
            else FilmEventFetcher(this, { fetchedFilmEvents ->
                populateList(fetchedFilmEvents)
                InsertFilmEventsIntoDatabaseTask(this).execute(fetchedFilmEvents)
            }, { volleyError ->
                Log.e(logTag, "$volleyError")
                showRetry()
            }).fetch()
        }).execute()!!
    }

    private fun populateList(filmEvents: List<FilmEvent>) = if (filmEvents.isNotEmpty()) {
        adapter.swapFilmEvents(filmEvents)
        genres = filmEvents.mapNotNull { it.genreTags?.asIterable() }.flatten().distinct().sorted()

        film_list.visibility = View.VISIBLE
        failed_to_load_events.visibility = View.GONE
        invalidateOptionsMenu()

        filmEvents.forEach { filmEvent ->
            Glide.with(this@MainActivity)
                    .load(filmEvent.imageThumbnail)
                    .load(filmEvent.imageOriginal)
                    .preload()
        }
    } else {
        showRetry()
    }

    private fun showRetry() {
        film_list.visibility = View.GONE
        failed_to_load_events.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    @SuppressLint("StaticFieldLeak")
    private fun enableDebugMode() {
        FilmEventDbHelper(this@MainActivity).apply {
            deleteDatabase(FilmEventDbHelper.DATABASE_NAME)
            close()
        }

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build())

        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                Glide.get(applicationContext).clearDiskCache()
            }
        }.execute()
    }
}
