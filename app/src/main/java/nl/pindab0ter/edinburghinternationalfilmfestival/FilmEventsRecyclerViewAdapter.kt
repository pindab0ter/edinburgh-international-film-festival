package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.main_list_content.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.ARG_DESCRIPTION
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.ARG_IMAGE_URL
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.ARG_SHOWINGS
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.ARG_TITLE
import nl.pindab0ter.edinburghinternationalfilmfestival.data.ImageFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatShowDate

class FilmEventsRecyclerViewAdapter(private val parentActivity: ListActivity, private val twoPane: Boolean) :
        RecyclerView.Adapter<FilmEventsRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    private val imageFetcher: ImageFetcher = ImageFetcher(parentActivity)

    private var filmEvents: List<FilmEvent>? = null

    init {
        onClickListener = View.OnClickListener { v ->
            val filmEvent = v.tag as FilmEvent
            val imageUrl = "https:${filmEvent.images?.versions?.original?.url}"
            val showings = filmEvent.performances?.map { formatShowDate(it.start) }?.toTypedArray()

            if (twoPane) {
                val fragment = DetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, filmEvent.title)
                        putString(ARG_DESCRIPTION, filmEvent.description)
                        putString(ARG_IMAGE_URL, imageUrl)
                        putStringArray(ARG_SHOWINGS, showings)
                    }
                }
                parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit()
            } else {
                val intent = Intent(v.context, DetailActivity::class.java).apply {
                    putExtra(ARG_TITLE, filmEvent.title)
                    putExtra(ARG_DESCRIPTION, filmEvent.description)
                    putExtra(ARG_IMAGE_URL, imageUrl)
                    putExtra(ARG_SHOWINGS, showings)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        filmEvents?.get(position).let { filmEvent ->
            holder.titleView.text = filmEvent?.title
            holder.firstShowingView.text = formatShowDate(filmEvent?.performances?.first()?.start)

            filmEvent?.let {
                imageFetcher.fetch("https:${filmEvent.images?.versions?.small320?.url}", { bitmap: Bitmap ->
                    holder.imageView.setImageBitmap(bitmap)
                })
            }

            with(holder.itemView) {
                tag = filmEvent
                setOnClickListener(onClickListener)
            }
        }
    }

    fun sortBy(criterion: String) {
        //@formatter:off
        filmEvents = filmEvents?.run { when (criterion) {
            TITLE_ASCENDING                   -> sortedBy { it.title }
            TITLE_DESCENDING                  -> sortedByDescending { it.title }
            FIRST_PERFORMANCE_DATE_ASCENDING  -> sortedBy { it.performances?.first()?.start }
            FIRST_PERFORMANCE_DATE_DESCENDING -> sortedByDescending { it.performances?.first()?.start }
            else -> throw RuntimeException()
        }}
        //@formatter:on
        notifyDataSetChanged()
    }

    fun swapFilmEvents(filmEvents: List<FilmEvent>) {
        this.filmEvents = filmEvents
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filmEvents?.count() ?: 0
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.film_list_image
        val titleView: TextView = view.film_list_title
        val firstShowingView: TextView = view.film_first_showing
    }

    companion object {
        const val TITLE_ASCENDING = "title_ascending"
        const val TITLE_DESCENDING = "title_descending"
        const val FIRST_PERFORMANCE_DATE_ASCENDING = "first_performance_date_ascending"
        const val FIRST_PERFORMANCE_DATE_DESCENDING = "first_performance_date_descending"
    }
}