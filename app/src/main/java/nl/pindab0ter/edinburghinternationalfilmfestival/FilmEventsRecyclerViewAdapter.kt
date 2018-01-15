package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.film_list_content.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmDetailFragment.Companion.ARG_FILM_DESCRIPTION
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmDetailFragment.Companion.ARG_FILM_TITLE
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

class FilmEventsRecyclerViewAdapter(private val parentActivity: FilmListActivity, private val twoPane: Boolean) :
        RecyclerView.Adapter<FilmEventsRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    private var filmEvents: List<FilmEvent>? = null

    init {
        onClickListener = View.OnClickListener { v ->
            val filmEvent = v.tag as FilmEvent
            if (twoPane) {
                val fragment = FilmDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_FILM_TITLE, filmEvent.title)
                        putString(ARG_FILM_DESCRIPTION, filmEvent.description)
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
            holder.idView.text = "$position"
            holder.contentView.text = filmEvent?.title

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

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val idView: TextView = mView.id_text
        val contentView: TextView = mView.content
    }
}