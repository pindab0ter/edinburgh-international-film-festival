package nl.pindab0ter.edinburghinternationalfilmfestival

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_schedule.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventViewModel

class ScheduleAdapter(fragment: Fragment) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>(), Observer<List<FilmEvent>> {

    private val logTag = ScheduleAdapter::class.simpleName

    private var performances: List<FilmEvent.Performance>? = null
    private var filmEvents: List<FilmEvent>? = null
        set(value) {
            field = value?.filter { it.performances?.any { it.scheduled ?: false } ?: false }
            performances = field?.flatMap { it.performances?.asIterable()!! }?.filter { it.scheduled == true }
        }

    init {
        ViewModelProviders.of(fragment.activity!!).get(FilmEventViewModel::class.java).filmEvents.apply {
            observe(fragment, this@ScheduleAdapter)
            filmEvents = value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val performance = performances?.get(position)
        val filmEvent: FilmEvent? = filmEvents?.find { it?.performances?.contains(performance) ?: false }

        holder.title.text = "${filmEvent?.title} ${performance?.start}"
    }

    override fun getItemCount(): Int = performances?.count() ?: 0

    override fun onChanged(filmEvents: List<FilmEvent>?) {
        this.filmEvents = filmEvents
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.tv_schedule_title
    }
}