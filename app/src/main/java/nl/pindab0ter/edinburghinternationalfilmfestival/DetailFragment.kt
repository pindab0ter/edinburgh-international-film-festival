package nl.pindab0ter.edinburghinternationalfilmfestival

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplay

/**
 * A fragment representing a single FilmEvent detail screen.
 * This fragment is either contained in a [ListActivity]
 * in two-pane mode (on tablets) or a [DetailActivity]
 * on handsets.
 */
class DetailFragment : Fragment() {
    var filmEvent: FilmEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments!!.getString(FILM_EVENT_CODE)

        filmEvent = FilmEventDAO(context!!).get(code)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.toolbar_layout?.title = filmEvent?.title

        val rootView = inflater.inflate(R.layout.fragment_detail, container, false) as LinearLayout

        Glide.with(context!!)
                .load(filmEvent?.imageOriginal)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        activity?.toolbar_layout?.background = resource
                    }
                })

        rootView.tv_detail_description.text = Html.fromHtml(filmEvent?.description)
        rootView.tv_detail_showings_label.text = resources.getQuantityString(R.plurals.showings, filmEvent?.performances?.size ?: 2)

        filmEvent?.performances?.forEach {
            val element = layoutInflater.inflate(R.layout.showings_list_item, rootView.showings_layout, false) as TextView
            element.text = it.start?.formatForDisplay()
            rootView.showings_layout.addView(element)
        }

        return rootView
    }

    companion object {
        const val FILM_EVENT_CODE = "film_event_code"
    }
}
