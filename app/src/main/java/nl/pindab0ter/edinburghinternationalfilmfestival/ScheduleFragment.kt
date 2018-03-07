package nl.pindab0ter.edinburghinternationalfilmfestival

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ScheduleFragment: Fragment() {
    private val logTag = ScheduleFragment::class.simpleName

    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ScheduleAdapter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        view.findViewById<RecyclerView>(R.id.rv_schedule).adapter = adapter
        return view
    }
}