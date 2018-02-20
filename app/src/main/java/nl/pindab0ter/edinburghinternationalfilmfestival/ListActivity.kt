package nl.pindab0ter.edinburghinternationalfilmfestival

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_master.*
import kotlinx.android.synthetic.main.film_list.*
import nl.pindab0ter.edinburghinternationalfilmfestival.R.layout.activity_master
import nl.pindab0ter.edinburghinternationalfilmfestival.R.menu.menu_list_activity
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.dummy.EDINBURGH_FILM_FESTIVAL_REPLY_FIRST
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.LongLog

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [DetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ListActivity : AppCompatActivity() {

    private val logTag = ListActivity::class.simpleName
    private val twoPane: Boolean // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
        get() = detail_container != null
    private lateinit var adapter: FilmEventsRecyclerViewAdapter
    private var genres: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_master)

        setSupportActionBar(toolbar)
        toolbar.title = title

        setupRecyclerView(film_list)
        fetchFilmEvents()
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

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = FilmEventsRecyclerViewAdapter(this, twoPane)
        recyclerView.adapter = adapter
    }

    private fun fetchFilmEvents() = FilmEventsFetcher(this, { filmEvents ->
        adapter.swapFilmEvents(filmEvents)
        genres = filmEvents.mapNotNull { it.genreTags?.asIterable() }.flatten().distinct().sorted()

        LongLog.d(logTag, EDINBURGH_FILM_FESTIVAL_REPLY_FIRST)
        LongLog.d(logTag, FilmEventsRequest.gson.toJson(filmEvents.first()))

        invalidateOptionsMenu()
    }).fetchOffline()
}
