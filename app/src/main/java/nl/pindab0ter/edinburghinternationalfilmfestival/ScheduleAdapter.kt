package nl.pindab0ter.edinburghinternationalfilmfestival

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.list_item_schedule.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventViewModel
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplayLong
import java.lang.ref.WeakReference

class ScheduleAdapter(fragment: Fragment, private val onClickListener: View.OnClickListener) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>(), Observer<List<FilmEvent>> {
    private val logTag = ScheduleAdapter::class.simpleName

    private var fragment: WeakReference<Fragment> = WeakReference(fragment)
    private val filmEventViewModel: FilmEventViewModel = ViewModelProviders.of(fragment.activity!!).get(FilmEventViewModel::class.java)

    private var performances: List<FilmEvent.Performance>? = null
    private var filmEvents: List<FilmEvent>? = null
        set(value) {
            field = value
                    ?.filter { it.performances?.any { it.scheduled ?: false } ?: false }
            performances = field
                    ?.flatMap { it.performances?.asIterable()!! }
                    ?.filter { it.scheduled == true }
                    ?.sortedBy { it.start }

            notifyDataSetChanged()
        }

    init {
        filmEventViewModel.filmEvents.observe(fragment, this@ScheduleAdapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_schedule, parent, false)
        return ViewHolder(view)
    }

    fun update() = filmEventViewModel.refresh()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val performance = performances?.get(position)
        val filmEvent: FilmEvent? = filmEvents?.find { it.performances?.contains(performance) ?: false }

        holder.title.text = filmEvent?.title
        holder.date.text = performance?.start?.formatForDisplayLong()
        holder.venueName.text = filmEvent?.venueName
        holder.venueAddress.text = filmEvent?.venueAddress

        with(holder.itemView) {
            tag = filmEvent?.code
            setOnClickListener(onClickListener)
        }

        fragment.get()?.let {
            Glide.with(it)
                    .load(filmEvent?.imageOriginal)
                    .apply(RequestOptions().centerCrop())
                    .into(holder.image)
        }
    }

    override fun getItemCount(): Int = performances?.count() ?: 0

    override fun onChanged(filmEvents: List<FilmEvent>?) {
        this.filmEvents = filmEvents
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.iv_schedule_image
        val date: TextView = view.tv_schedule_date
        val title: TextView = view.tv_schedule_title
        val venueName: TextView = view.tv_venue_name
        val venueAddress: TextView = view.tv_venue_address
    }
}