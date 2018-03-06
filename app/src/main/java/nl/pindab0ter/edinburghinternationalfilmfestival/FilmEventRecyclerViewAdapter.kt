package nl.pindab0ter.edinburghinternationalfilmfestival

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.film_event_list_item.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventViewModel
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplay
import java.lang.ref.WeakReference

class FilmEventRecyclerViewAdapter(fragment: Fragment, private val onClickListener: View.OnClickListener) : RecyclerView.Adapter<FilmEventRecyclerViewAdapter.ViewHolder>(), Observer<List<FilmEvent>> {
    private var fragment: WeakReference<Fragment> = WeakReference(fragment)
    private var unfilteredFilmEvents: List<FilmEvent>? = null

    private var filmEvents: List<FilmEvent>? = null
    private var sortCriterion: Int = R.id.sort_title_ascending

    private var filteredByGenre: CharSequence? = null

    init {
        ViewModelProviders.of(fragment.activity!!).get(FilmEventViewModel::class.java).filmEvents.apply {
            observe(fragment, this@FilmEventRecyclerViewAdapter)
            filmEvents = value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.film_event_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        filmEvents?.get(position).let { filmEvent ->
            holder.titleView.text = filmEvent?.title
            holder.firstShowingView.text = filmEvent?.performances?.first()?.start?.formatForDisplay()

            with(holder.itemView) {
                tag = filmEvent?.code
                setOnClickListener(onClickListener)
            }

            fragment.get()?.let {
                Glide.with(it)
                        .load(filmEvent?.imageThumbnail)
                        .into(holder.imageView)
            }
        }
    }

    fun sortBy(itemId: Int?) {
        if (itemId == null) return

        //@formatter:off
        filmEvents= filmEvents?.run { when (itemId) {
            R.id.sort_title_ascending   -> sortedBy { it.title }
            R.id.sort_title_descending  -> sortedByDescending { it.title }
            R.id.sort_date_ascending    -> sortedBy { it.performances?.first()?.start }
            R.id.sort_date_descending   -> sortedByDescending { it.performances?.first()?.start }
            else -> throw RuntimeException("Cannot sort on itemId $itemId")
        }}
        //@formatter:on

        sortCriterion = itemId
        notifyDataSetChanged()
    }

    fun filterBy(genre: CharSequence?) {
        if (genre == filteredByGenre) return

        val predicate: (FilmEvent) -> Boolean = { it.genreTags?.contains(genre) ?: false }

        filmEvents = if (genre == fragment.get()?.getString(R.string.filter_none)) unfilteredFilmEvents
        else unfilteredFilmEvents?.filter(predicate)
        filteredByGenre = genre

        sortBy(sortCriterion)
        notifyDataSetChanged()
    }

    override fun onChanged(filmEvents: List<FilmEvent>?) {
        this.unfilteredFilmEvents = filmEvents
        this.filmEvents = unfilteredFilmEvents
        sortBy(sortCriterion)
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