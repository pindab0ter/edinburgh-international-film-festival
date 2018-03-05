package nl.pindab0ter.edinburghinternationalfilmfestival

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.main_list_item.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.FILM_EVENT_CODE
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplay

class FilmEventRecyclerViewAdapter(private val parentActivity: MainActivity, private val twoPane: Boolean) : RecyclerView.Adapter<FilmEventRecyclerViewAdapter.ViewHolder>(), Observer<List<FilmEvent>>, View.OnClickListener {
    private var unfilteredFilmEvents: List<FilmEvent>? = null
    private var filmEvents: List<FilmEvent>? = null

    private var sortCriterion: Int = R.id.sort_title_ascending
    private var filteredByGenre: CharSequence? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        filmEvents?.get(position).let { filmEvent ->
            holder.titleView.text = filmEvent?.title
            holder.firstShowingView.text = filmEvent?.performances?.first()?.start?.formatForDisplay()


            with(holder.itemView) {
                tag = filmEvent?.code
                setOnClickListener(this@FilmEventRecyclerViewAdapter)
            }

            Glide.with(parentActivity)
                    .load(filmEvent?.imageOriginal)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageView)
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

        filmEvents = if (genre == parentActivity.getString(R.string.filter_none)) unfilteredFilmEvents
        else unfilteredFilmEvents?.filter(predicate)
        filteredByGenre = genre

        sortBy(sortCriterion)
        notifyDataSetChanged()
    }

    override fun onClick(view: View?) {
        val filmEventCode = view?.tag as String

        if (twoPane) {
            val fragment = DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(FILM_EVENT_CODE, filmEventCode)
                }
            }
            parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit()
        } else {
            val intent = Intent(view.context, DetailActivity::class.java).apply {
                putExtra(FILM_EVENT_CODE, filmEventCode)
            }
            val options = ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0, view.width, view.height).toBundle()
            view.context.startActivity(intent, options)
        }
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