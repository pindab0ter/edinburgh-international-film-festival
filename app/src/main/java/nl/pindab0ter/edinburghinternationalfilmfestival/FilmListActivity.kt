package nl.pindab0ter.edinburghinternationalfilmfestival

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_film_list.*

import kotlinx.android.synthetic.main.film_list.*
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmEventsRecyclerViewAdapter.Companion.FIRST_PERFORMANCE_DATE_ASCENDING
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmEventsRecyclerViewAdapter.Companion.FIRST_PERFORMANCE_DATE_DESCENDING
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmEventsRecyclerViewAdapter.Companion.TITLE_ASCENDING
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmEventsRecyclerViewAdapter.Companion.TITLE_DESCENDING
import nl.pindab0ter.edinburghinternationalfilmfestival.R.id.*
import nl.pindab0ter.edinburghinternationalfilmfestival.R.layout.activity_film_list
import nl.pindab0ter.edinburghinternationalfilmfestival.R.menu.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsFetcher

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [FilmDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class FilmListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private val twoPane: Boolean
        get() = film_detail_container != null
    private lateinit var adapter: FilmEventsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_film_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        setupRecyclerView(film_list)
        fetchFilmEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            sort_title_ascending -> adapter.sortBy(TITLE_ASCENDING)
            sort_title_descending -> adapter.sortBy(TITLE_DESCENDING)
            sort_date_ascending -> adapter.sortBy(FIRST_PERFORMANCE_DATE_ASCENDING)
            sort_date_descending -> adapter.sortBy(FIRST_PERFORMANCE_DATE_DESCENDING)
            else -> return false
        }
        return true
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = FilmEventsRecyclerViewAdapter(this, twoPane)
        recyclerView.adapter = adapter
    }

    private fun fetchFilmEvents() = FilmEventsFetcher(this, { filmEvents ->
        adapter.swapFilmEvents(filmEvents)
    }).fetch()
}
