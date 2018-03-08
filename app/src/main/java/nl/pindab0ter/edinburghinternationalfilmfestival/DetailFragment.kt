package nl.pindab0ter.edinburghinternationalfilmfestival

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_detail.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent

/**
 * A fragment representing a single FilmEvent detail screen.
 * This fragment is either contained in a [MainActivity]
 * in two-pane mode (on tablets) or a [DetailActivity]
 * on handsets.
 */
class DetailFragment : Fragment() {
    private var filmEvent: FilmEvent? = null
    private lateinit var performanceListAdapter: PerformanceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments!!.getString(FILM_EVENT_CODE)

        filmEvent = FilmEventDAO(context!!).get(code)
        performanceListAdapter = PerformanceListAdapter(activity!!, R.layout.list_item_performance, filmEvent?.performances.orEmpty())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false)

        rootView.showings_list.adapter = performanceListAdapter

        rootView.tv_detail_description.text = Html.fromHtml(filmEvent?.description)
        rootView.tv_detail_showings_label.text = resources.getQuantityString(R.plurals.showings, filmEvent?.performances?.size ?: 2)
        rootView.tv_detail_venue_name.text = filmEvent?.venueName
        rootView.tv_detail_venue_address.text = filmEvent?.venueAddress

        return rootView
    }

    companion object {
        const val FILM_EVENT_CODE = "film_event_code"
    }
}
