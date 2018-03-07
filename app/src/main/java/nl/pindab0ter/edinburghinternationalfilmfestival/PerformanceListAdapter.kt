package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.list_item_performance.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplay

class PerformanceListAdapter(context: Context, resource: Int, performances: Array<out FilmEvent.Performance>) : ArrayAdapter<FilmEvent.Performance>(context, resource, performances) {
    private val filmEventDAO = FilmEventDAO(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val performance = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_performance, parent, false)

        view.tv_performance_date.text = performance.start?.formatForDisplay()

        view.tb_schedule_performance.apply {
            tag = position
            isChecked = performance.scheduled ?: false

            setOnClickListener { view ->
                val clickedPerformance = getItem(view.tag as Int)
                clickedPerformance.scheduled = this.isChecked

                filmEventDAO.update(clickedPerformance)
            }
        }
        return view
    }
}