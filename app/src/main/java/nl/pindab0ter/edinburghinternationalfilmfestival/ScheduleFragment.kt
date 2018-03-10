package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*

class ScheduleFragment : Fragment(), BottomNavigationView.OnNavigationItemReselectedListener, View.OnClickListener {
    private val logTag = ScheduleFragment::class.simpleName

    private lateinit var adapter: ScheduleAdapter
    private val twoPane: Boolean get() = detail_container != null
    private var firstRun = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ScheduleAdapter(this, this)
        activity?.bottom_nav_bar?.setOnNavigationItemReselectedListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        view.rv_schedule.adapter = adapter
        return view
    }

    override fun onStart() {
        activity?.tv_appbar_title_schedule?.visibility = View.VISIBLE
        super.onStart()
    }

    override fun onResume() {
        if (!firstRun) adapter.update()
        else firstRun = false
        super.onResume()
    }

    override fun onStop() {
        activity?.tv_appbar_title_schedule?.visibility = View.GONE
        super.onStop()
    }

    override fun onClick(view: View?) {
        val filmEventCode = view?.tag as String

        if (twoPane) {
            val fragment = DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DetailFragment.FILM_EVENT_CODE, filmEventCode)
                }
            }

            activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.detail_container, fragment)
                    ?.commit()
        } else {
            val intent = Intent(view.context, DetailActivity::class.java).apply {
                putExtra(DetailFragment.FILM_EVENT_CODE, filmEventCode)
            }
            val options = ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0, view.width, view.height).toBundle()
            view.context.startActivity(intent, options)
        }
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        rv_schedule.smoothScrollToPosition(rv_schedule.top)
    }
}