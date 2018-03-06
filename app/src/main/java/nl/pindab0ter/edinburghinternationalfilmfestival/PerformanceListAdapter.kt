package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.performance_list_item.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplay

class PerformanceListAdapter(context: Context, resource: Int, performances: Array<out FilmEvent.Performance>) : ArrayAdapter<FilmEvent.Performance>(context, resource, performances) {
    private val filmEventDAO = FilmEventDAO(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val performance = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.performance_list_item, parent, false)

        view.performance_list_item_date.text = performance.start?.formatForDisplay()

        view.performance_list_item_button_add.apply {
            tag = position
            visibility = if (performance.scheduled == false) View.VISIBLE else View.GONE

            setOnClickListener { view ->
                val clickedPerformance = getItem(view.tag as Int)
                clickedPerformance.scheduled = true
                filmEventDAO.update(clickedPerformance)

                (view.parent as View).apply {
                    performance_list_item_button_add.visibility = View.GONE
                    performance_list_item_button_remove.visibility = View.VISIBLE
                }
            }
        }

        view.performance_list_item_button_remove.apply {
            tag = position
            visibility = if (performance.scheduled == true) View.VISIBLE else View.GONE
            setOnClickListener { view ->
                val clickedPerformance = getItem(view.tag as Int)
                clickedPerformance.scheduled = false
                filmEventDAO.update(clickedPerformance)

                (view.parent as View).apply {
                    performance_list_item_button_add.visibility = View.VISIBLE
                    performance_list_item_button_remove.visibility = View.GONE
                }
            }
        }
        return view
    }
}