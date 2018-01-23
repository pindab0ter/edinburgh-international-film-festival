package nl.pindab0ter.edinburghinternationalfilmfestival

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_film_detail.*
import kotlinx.android.synthetic.main.film_detail.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.ImageFetcher

/**
 * A fragment representing a single FilmEvent detail screen.
 * This fragment is either contained in a [FilmListActivity]
 * in two-pane mode (on tablets) or a [FilmDetailActivity]
 * on handsets.
 */
class FilmDetailFragment : Fragment() {

    private lateinit var filmTitle: String
    private lateinit var filmDescription: String
    private lateinit var filmShowings: Array<String>
    private lateinit var filmImageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.keySet()?.forEach { parseKey(it) }
    }

    private fun parseKey(key: String) = when (key) {
        ARG_FILM_TITLE -> filmTitle = arguments!!.getString(ARG_FILM_TITLE)
        ARG_FILM_DESCRIPTION -> filmDescription = arguments!!.getString(ARG_FILM_DESCRIPTION)
        ARG_FILM_SHOWINGS -> filmShowings = arguments!!.getStringArray(ARG_FILM_SHOWINGS)
        ARG_FILM_IMAGE_URL -> filmImageUrl = arguments!!.getString(ARG_FILM_IMAGE_URL)
        else -> Unit
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.toolbar_layout?.title = filmTitle

        val rootView = inflater.inflate(R.layout.film_detail, container, false) as LinearLayout

        ImageFetcher(context!!).fetch(filmImageUrl) { bitmap: Bitmap ->
            activity?.toolbar_layout?.background = BitmapDrawable(resources, bitmap)
            activity?.toolbar_layout?.contentScrim = BitmapDrawable(resources, bitmap)
        }

        rootView.film_detail_description.text = Html.fromHtml(filmDescription)
        rootView.film_showings_label.text = resources.getQuantityString(R.plurals.showings, filmShowings.size)

        filmShowings.forEach {
            val element = layoutInflater.inflate(R.layout.showings_list_item, rootView.film_detail_showings, false) as TextView
            element.text = it
            rootView.film_detail_showings.addView(element)
        }

        return rootView
    }

    companion object {
        const val ARG_FILM_TITLE = "film_title"
        const val ARG_FILM_DESCRIPTION = "film_description"
        const val ARG_FILM_SHOWINGS = "film_showings"
        const val ARG_FILM_IMAGE_URL = "film_image_url"
    }
}
