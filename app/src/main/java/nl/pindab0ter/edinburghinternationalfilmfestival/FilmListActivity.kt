package nl.pindab0ter.edinburghinternationalfilmfestival

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_film_list.*

import kotlinx.android.synthetic.main.film_list.*
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
    private var twoPane: Boolean = false
    private lateinit var adapter: FilmEventsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (film_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(film_list)
        fetchFilmEvents()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = FilmEventsRecyclerViewAdapter(this, twoPane)
        recyclerView.adapter = adapter
    }

    private fun fetchFilmEvents() = FilmEventsFetcher(this, { filmEvents ->
        adapter.swapFilmEvents(filmEvents)
    }).fetch()
}
