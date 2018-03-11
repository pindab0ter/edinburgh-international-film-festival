package nl.pindab0ter.edinburghinternationalfilmfestival

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_film_event.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventViewModel
import java.lang.ref.WeakReference

class FilmEventListAdapter(fragment: Fragment, private val onClickListener: View.OnClickListener) : RecyclerView.Adapter<FilmEventListAdapter.ViewHolder>(), Observer<List<FilmEvent>> {
    private val logTag = FilmEventListAdapter::class.simpleName

    private val twoPane: Boolean get() = fragment.get()?.view?.findViewById<FrameLayout>(R.id.detail_container) != null
    private var fragment: WeakReference<Fragment> = WeakReference(fragment)
    private var unfilteredFilmEvents: List<FilmEvent>? = null
    private val filmEventViewModel: FilmEventViewModel = ViewModelProviders.of(fragment.activity!!).get(FilmEventViewModel::class.java)

    private var filmEvents: List<FilmEvent>? = null
    private var sortCriterion: Int = R.id.sort_title_ascending

    private var filteredByGenre: CharSequence? = null
    private var selectedPosition = RecyclerView.NO_POSITION

    init {
        filmEventViewModel.filmEvents.observe(fragment, this@FilmEventListAdapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_film_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filmEvent = filmEvents?.get(position)

        with(holder) {
            title.text = filmEvent?.title
            description.text = Html.fromHtml(filmEvent?.description)
        }

        with(holder.itemView) {
            tag = filmEvent?.code

            if (position == selectedPosition && twoPane) {
                setBackgroundColor(resources.getColor(R.color.colorPrimaryTransparent, resources.newTheme()))
            } else {
                setBackgroundColor(resources.getColor(android.R.color.white, resources.newTheme()))
            }

            setOnClickListener { view ->
                onClickListener.onClick(view)
                if (holder.adapterPosition != RecyclerView.NO_POSITION && holder.adapterPosition != selectedPosition) {
                    selectedPosition = holder.adapterPosition
                    notifyDataSetChanged()
                }
            }
        }

        fragment.get()?.let {
            Glide.with(it)
                    .load(filmEvent?.imageThumbnail)
                    .into(holder.image)
        }
    }

    override fun onChanged(filmEvents: List<FilmEvent>?) {
        this.unfilteredFilmEvents = filmEvents
        this.filmEvents = unfilteredFilmEvents
        sortBy(sortCriterion)
        notifyDataSetChanged()
    }

    fun update() = filmEventViewModel.refresh()

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

    override fun getItemCount(): Int = filmEvents?.count() ?: 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.iv_film_event_list_image
        val title: TextView = view.tv_film_event_list_item_title
        val description: TextView = view.tv_film_event_list_description
    }
}