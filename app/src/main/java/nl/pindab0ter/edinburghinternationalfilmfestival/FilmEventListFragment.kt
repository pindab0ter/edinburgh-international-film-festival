package nl.pindab0ter.edinburghinternationalfilmfestival

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.view.*
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_film_event_list.*
import kotlinx.android.synthetic.main.fragment_film_event_list.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventViewModel

class FilmEventListFragment : Fragment(), Observer<List<FilmEvent>>, View.OnClickListener, BottomNavigationView.OnNavigationItemReselectedListener {
    private val logTag = FilmEventListFragment::class.simpleName

    private lateinit var adapter: FilmEventListAdapter
    private lateinit var popupMenu: PopupMenu
    private var genres: List<String>? = null
    private var firstRun = true

    private val twoPane: Boolean get() = detail_container != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = FilmEventListAdapter(this, this)
        activity?.bottom_nav_bar?.setOnNavigationItemReselectedListener(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_film_event_list, container, false)
        view.rv_film_events?.adapter = adapter
        return view
    }

    override fun onStart() {
        activity?.button_toolbar_filter?.visibility = View.VISIBLE
        ViewModelProviders.of(activity!!).get(FilmEventViewModel::class.java).filmEvents.observe(this, this)
        super.onStart()
    }

    override fun onResume() {
        if (!firstRun) adapter.update()
        else firstRun = false
        super.onResume()
    }

    override fun onStop() {
        activity?.button_toolbar_filter?.visibility = View.GONE
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.groupId) {
        R.id.sort_group -> {
            adapter.sortBy(item.itemId)
            item.isChecked = true
            true
        }
        else -> false
    }

    override fun onChanged(filmEvents: List<FilmEvent>?) {
        if (filmEvents != null && filmEvents.isNotEmpty()) {
            genres = filmEvents.mapNotNull { it.genreTags?.asIterable() }.flatten().distinct().sorted()
        }

        activity?.invalidateOptionsMenu()
    }

    override fun onClick(view: View?) {
        val filmEventCode = view?.tag as String

        if (twoPane) {
            val fragment = DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailFragment.FILM_EVENT_CODE, filmEventCode)
                }
            }

            activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.detail_container, fragment)
                    ?.commit()
        } else {
            val intent = Intent(view.context, DetailActivity::class.java).apply {
                putExtra(DetailFragment.FILM_EVENT_CODE, filmEventCode)
            }
            val options = ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0, view.width, view.height).toBundle()
            view.context.startActivity(intent, options)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_sort, menu)

        popupMenu = PopupMenu(context?.applicationContext, activity?.button_toolbar_filter).apply {
            inflater?.inflate(R.menu.menu_filter, this.menu)
            activity?.button_toolbar_filter?.setOnClickListener { popupMenu.show() }

            with(this.menu) {
                if (genres.orEmpty().isEmpty()) return@with

                findItem(nl.pindab0ter.edinburghinternationalfilmfestival.R.id.filter_failed_to_load)?.isVisible = false
                findItem(nl.pindab0ter.edinburghinternationalfilmfestival.R.id.filter_all)?.isVisible = true

                genres?.forEachIndexed { index, genre ->
                    add(nl.pindab0ter.edinburghinternationalfilmfestival.R.id.filter_group, index, index, genre)
                }
            }

            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.filter_failed_to_load) return@setOnMenuItemClickListener false
                adapter.filterBy(item.title)
                activity?.button_toolbar_filter?.text = item.title
                item.isChecked = true
                true
            }
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        rv_film_events.smoothScrollToPosition(rv_film_events.top)
    }
}
