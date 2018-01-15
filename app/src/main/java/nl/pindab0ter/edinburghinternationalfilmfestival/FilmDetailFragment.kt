package nl.pindab0ter.edinburghinternationalfilmfestival

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var filmImageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(ARG_FILM_TITLE)) {
            filmTitle = arguments.getString(ARG_FILM_TITLE)
            activity?.toolbar_layout?.title = filmTitle
        }

        if (arguments.containsKey(ARG_FILM_DESCRIPTION)) {
            filmDescription = arguments.getString(ARG_FILM_DESCRIPTION)
        }

        if (arguments.containsKey(ARG_FILM_IMAGE_URL)) {
            filmImageUrl = arguments.getString(ARG_FILM_IMAGE_URL)
            ImageFetcher(context).fetch(filmImageUrl) { bitmap: Bitmap ->
                activity?.toolbar_layout?.background = BitmapDrawable(resources, bitmap)
                activity?.toolbar_layout?.contentScrim = BitmapDrawable(resources, bitmap)
            }
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
        const val ARG_FILM_IMAGE_URL = "film_image_url"
    }
}
