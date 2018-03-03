package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_detail.*
import nl.pindab0ter.edinburghinternationalfilmfestival.DetailFragment.Companion.FILM_EVENT_CODE
import nl.pindab0ter.edinburghinternationalfilmfestival.RatingDialogFragment.Companion.DIALOG_TAG
import nl.pindab0ter.edinburghinternationalfilmfestival.RatingDialogFragment.Companion.DIALOG_WEBSITE
import nl.pindab0ter.edinburghinternationalfilmfestival.RatingDialogFragment.Companion.DIALOG_TITLE
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

/**
 * An activity representing a single FilmEvent detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [ListActivity].
 */
class DetailActivity : AppCompatActivity() {
    private var filmEvent: FilmEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filmEvent = FilmEventDAO(applicationContext).get(intent.getStringExtra(FILM_EVENT_CODE))

        setContentView(R.layout.activity_detail)

        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = filmEvent?.title

        Glide.with(this)
                .load(filmEvent?.imageOriginal)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        toolbar_layout?.background = resource
                    }
                })

        fab.setOnClickListener {
            RatingDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(DIALOG_TITLE, filmEvent?.title)
                    putString(DIALOG_WEBSITE, filmEvent?.website.toString())
                }
            }.show(fragmentManager, DIALOG_TAG)
        }


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val detailFragment = DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(FILM_EVENT_CODE, intent.getStringExtra(FILM_EVENT_CODE))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.detail_container, detailFragment)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
