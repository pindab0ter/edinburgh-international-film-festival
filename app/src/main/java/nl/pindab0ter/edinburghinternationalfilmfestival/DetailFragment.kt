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
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.network.ImageFetcher

/**
 * A fragment representing a single FilmEvent detail screen.
 * This fragment is either contained in a [ListActivity]
 * in two-pane mode (on tablets) or a [DetailActivity]
 * on handsets.
 */
class DetailFragment : Fragment() {

    private lateinit var title: String
    private lateinit var description: String
    private lateinit var showings: Array<String>
    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.keySet()?.forEach { parseKey(it) }
    }

    private fun parseKey(key: String) = when (key) {
        DETAIL_TITLE -> title = arguments!!.getString(DETAIL_TITLE)
        DETAIL_DESCRIPTION -> description = arguments!!.getString(DETAIL_DESCRIPTION)
        DETAIL_SHOWINGS -> showings = arguments!!.getStringArray(DETAIL_SHOWINGS)
        DETAIL_IMAGE_URL -> imageUrl = arguments!!.getString(DETAIL_IMAGE_URL)
        else -> Unit
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.toolbar_layout?.title = title

        val rootView = inflater.inflate(R.layout.fragment_detail, container, false) as LinearLayout

        ImageFetcher(context!!).fetch(imageUrl) { bitmap: Bitmap ->
            activity?.toolbar_layout?.background = BitmapDrawable(resources, bitmap)
            activity?.toolbar_layout?.contentScrim = BitmapDrawable(resources, bitmap)
        }

        rootView.tv_detail_description.text = Html.fromHtml(description)
        rootView.tv_detail_showings_label.text = resources.getQuantityString(R.plurals.showings, showings.size)

        showings.forEach {
            val element = layoutInflater.inflate(R.layout.showings_list_item, rootView.showings_layout, false) as TextView
            element.text = it
            rootView.showings_layout.addView(element)
        }

        return rootView
    }

    companion object {
        const val DETAIL_TITLE = "detail_title"
        const val DETAIL_DESCRIPTION = "detail_description"
        const val DETAIL_SHOWINGS = "detail_showings"
        const val DETAIL_IMAGE_URL = "detail_image_url"
    }
}
