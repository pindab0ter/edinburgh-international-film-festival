package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.performance_list_item.view.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDisplay

class PerformanceListAdapter(context: Context, resource: Int, objects: Array<out FilmEvent.Performance>) : ArrayAdapter<FilmEvent.Performance>(context, resource, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val performance = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.performance_list_item, parent, false)

        view.performance_list_item_date.text = performance.start?.formatForDisplay()

        if (performance.scheduled == false) view.performance_list_item_button_add.visibility = View.VISIBLE
        else view.performance_list_item_button_remove.visibility = View.VISIBLE

        return view
    }
}