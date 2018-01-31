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
import kotlinx.android.synthetic.main.main_list_item.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.DETAIL_DESCRIPTION
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.DETAIL_IMAGE_URL
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.DETAIL_SHOWINGS
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.DETAIL_TITLE
import nl.pindab0ter.edinburghinternationalfilmfestival.RatingDialogFragment.Companion.DIALOG_WEBSITE
import nl.pindab0ter.edinburghinternationalfilmfestival.data.ImageFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplay

class FilmEventsRecyclerViewAdapter(private val parentActivity: ListActivity, private val twoPane: Boolean) :
        RecyclerView.Adapter<FilmEventsRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    private val imageFetcher: ImageFetcher = ImageFetcher(parentActivity)

    private var unfilteredFilmEvents: List<FilmEvent>? = null
    private var filmEvents: List<FilmEvent>? = null

    private var sortedBy: Int? = null
    private var filteredByGenre: CharSequence? = null

    init {
        onClickListener = View.OnClickListener { v ->
            val filmEvent = v.tag as FilmEvent
            val imageUrl = "https:${filmEvent.images?.versions?.original?.url}"
            val showings = filmEvent.performances?.map { it.start?.formatForDisplay() }?.toTypedArray()

            if (twoPane) {
                val fragment = DetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(DETAIL_TITLE, filmEvent.title)
                        putString(DETAIL_DESCRIPTION, filmEvent.description)
                        putString(DETAIL_IMAGE_URL, imageUrl)
                        putString(DIALOG_WEBSITE, filmEvent.website)
                        putStringArray(DETAIL_SHOWINGS, showings)
                    }
                }
                parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit()
            } else {
                val intent = Intent(v.context, DetailActivity::class.java).apply {
                    putExtra(DETAIL_TITLE, filmEvent.title)
                    putExtra(DETAIL_DESCRIPTION, filmEvent.description)
                    putExtra(DETAIL_IMAGE_URL, imageUrl)
                    putExtra(DIALOG_WEBSITE, filmEvent.website)
                    putExtra(DETAIL_SHOWINGS, showings)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        filmEvents?.get(position).let { filmEvent ->
            holder.titleView.text = filmEvent?.title
            holder.firstShowingView.text = filmEvent?.performances?.first()?.start?.formatForDisplay()

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

    fun sortBy(itemId: Int?) {
        if (itemId == null) return

        //@formatter:off
        filmEvents = filmEvents?.run { when (itemId) {
            R.id.sort_title_ascending   -> sortedBy { it.title }
            R.id.sort_title_descending  -> sortedByDescending { it.title }
            R.id.sort_date_ascending    -> sortedBy { it.performances?.first()?.start }
            R.id.sort_date_descending   -> sortedByDescending { it.performances?.first()?.start }
            else -> throw RuntimeException("Cannot sort on itemId $itemId")
        }}
        //@formatter:on

        sortedBy = itemId
        notifyDataSetChanged()
    }

    fun filterBy(genre: CharSequence?) {
        if (genre == filteredByGenre) return

        val predicate: (FilmEvent) -> Boolean = { it.genreTags?.contains(genre) ?: false }

        filmEvents = if (genre == parentActivity.getString(R.string.filter_none)) unfilteredFilmEvents
        else unfilteredFilmEvents?.filter(predicate)
        filteredByGenre = genre

        sortBy(sortedBy)
        notifyDataSetChanged()
    }

    fun swapFilmEvents(filmEvents: List<FilmEvent>) {
        this.unfilteredFilmEvents = filmEvents
        this.filmEvents = unfilteredFilmEvents
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
}