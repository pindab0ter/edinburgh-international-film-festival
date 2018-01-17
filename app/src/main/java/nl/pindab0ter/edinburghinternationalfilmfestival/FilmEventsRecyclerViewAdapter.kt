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
import kotlinx.android.synthetic.main.film_list_content.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmDetailFragment.Companion.ARG_FILM_DESCRIPTION
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmDetailFragment.Companion.ARG_FILM_IMAGE_URL
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmDetailFragment.Companion.ARG_FILM_TITLE
import nl.pindab0ter.edinburghinternationalfilmfestival.data.ImageFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

class FilmEventsRecyclerViewAdapter(private val parentActivity: FilmListActivity, private val twoPane: Boolean) :
        RecyclerView.Adapter<FilmEventsRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    private val imageFetcher: ImageFetcher = ImageFetcher(parentActivity)

    private var filmEvents: List<FilmEvent>? = null

    init {
        onClickListener = View.OnClickListener { v ->
            val filmEvent = v.tag as FilmEvent
            val filmImageUrl = "https:${filmEvent.images?.versions?.original?.url}"
            if (twoPane) {
                val fragment = FilmDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_FILM_TITLE, filmEvent.title)
                        putString(ARG_FILM_DESCRIPTION, filmEvent.description)
                        putString(ARG_FILM_IMAGE_URL, filmImageUrl)
                    }
                }
                parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.film_detail_container, fragment)
                        .commit()
            } else {
                val intent = Intent(v.context, FilmDetailActivity::class.java).apply {
                    putExtra(ARG_FILM_TITLE, filmEvent.title)
                    putExtra(ARG_FILM_DESCRIPTION, filmEvent.description)
                    putExtra(ARG_FILM_IMAGE_URL, filmImageUrl)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.film_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        filmEvents?.get(position).let { filmEvent ->
            holder.titleView.text = filmEvent?.title

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
    }
}