package nl.pindab0ter.edinburghinternationalfilmfestival

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.lifecycle.ViewModelStoreOwner
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDbHelper
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventViewModel
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

class MainActivity : AppCompatActivity(), Observer<List<FilmEvent>>, ViewModelStoreOwner {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableDebugMode()

        setContentView(R.layout.activity_main)

        ViewModelProviders.of(this).get(FilmEventViewModel::class.java).filmEvents.observe(this, this)

        if (fragment_container != null && savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, FilmEventListFragment()).commit()
        }

        setSupportActionBar(findViewById(R.id.toolbar_main_activity))
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onChanged(filmEvents: List<FilmEvent>?) = if (filmEvents != null && filmEvents.isNotEmpty()) {
        fragment_container.visibility = View.VISIBLE
        load_failed.visibility = View.GONE

        filmEvents.forEach { filmEvent ->
            Glide.with(this@MainActivity)
                    .load(filmEvent.imageThumbnail)
                    .preload()

            Glide.with(this@MainActivity)
                    .downloadOnly()
                    .load(filmEvent.imageOriginal)
                    .preload()
        }
    } else {
        fragment_container.visibility = View.GONE
        load_failed.visibility = View.VISIBLE
    }

    private fun enableDebugMode() {
        FilmEventDbHelper(this@MainActivity).apply {
            deleteDatabase(FilmEventDbHelper.DATABASE_NAME)
            close()
        }

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build())

        class ClearDiskCacheTask : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                Glide.get(applicationContext).clearDiskCache()
            }
        }

        ClearDiskCacheTask().execute()
    }
}
