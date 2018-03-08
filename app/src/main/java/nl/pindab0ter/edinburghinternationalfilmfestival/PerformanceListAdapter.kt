package nl.pindab0ter.edinburghinternationalfilmfestival

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.list_item_performance.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventViewModel
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplayShort
import java.lang.ref.WeakReference

class PerformanceListAdapter(activity: FragmentActivity, resource: Int, performances: Array<out FilmEvent.Performance>) : ArrayAdapter<FilmEvent.Performance>(activity, resource, performances) {
    private val activity: WeakReference<FragmentActivity> = WeakReference(activity)
    private val filmEventDAO = FilmEventDAO(activity.applicationContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val performance = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_performance, parent, false)

        view.tv_performance_date.text = performance.start?.formatForDisplayShort()

        view.tb_schedule_performance.apply {
            tag = position
            isChecked = performance.scheduled ?: false

            setOnClickListener { view ->
                val clickedPerformance = getItem(view.tag as Int)
                clickedPerformance.scheduled = this.isChecked

                activity.get()?.let { ViewModelProviders.of(it).get(FilmEventViewModel::class.java).refresh() }
                filmEventDAO.update(clickedPerformance)
            }
        }
        return view
    }
}