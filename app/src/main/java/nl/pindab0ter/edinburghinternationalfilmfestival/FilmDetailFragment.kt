package nl.pindab0ter.edinburghinternationalfilmfestival

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import nl.pindab0ter.edinburghinternationalfilmfestival.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_film_detail.*
import kotlinx.android.synthetic.main.film_detail.view.*

/**
 * A fragment representing a single FilmEvent detail screen.
 * This fragment is either contained in a [FilmListActivity]
 * in two-pane mode (on tablets) or a [FilmDetailActivity]
 * on handsets.
 */
class FilmDetailFragment : Fragment() {

    private var filmTitle: String? = null
    private var filmDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(ARG_FILM_TITLE)) {
            filmTitle = arguments.getString(ARG_FILM_TITLE)
            activity?.toolbar_layout?.title = filmTitle
        }

        if (arguments.containsKey(ARG_FILM_DESCRIPTION)) {
            filmDescription = arguments.getString(ARG_FILM_DESCRIPTION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.film_detail, container, false)

        rootView.film_detail.text = Html.fromHtml(filmDescription)

        return rootView
    }

    companion object {
        const val ARG_FILM_TITLE = "film_title"
        const val ARG_FILM_DESCRIPTION = "film_description"
    }
}
