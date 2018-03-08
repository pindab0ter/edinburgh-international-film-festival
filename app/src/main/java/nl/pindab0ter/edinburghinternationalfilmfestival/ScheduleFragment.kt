package nl.pindab0ter.edinburghinternationalfilmfestival

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment: Fragment(), BottomNavigationView.OnNavigationItemReselectedListener {
    private val logTag = ScheduleFragment::class.simpleName

    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ScheduleAdapter(this)
        activity?.bottom_nav_bar?.setOnNavigationItemReselectedListener(this)
    }

    override fun onStart() {
        activity?.tv_appbar_title_schedule?.visibility = View.VISIBLE
        super.onStart()
    }

    override fun onStop() {
        activity?.tv_appbar_title_schedule?.visibility = View.GONE
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        view.findViewById<RecyclerView>(R.id.rv_schedule).adapter = adapter
        return view
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        rv_schedule.smoothScrollToPosition(rv_schedule.top)
    }
}