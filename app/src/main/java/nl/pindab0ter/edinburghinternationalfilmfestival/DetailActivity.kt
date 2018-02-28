package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
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
        setContentView(R.layout.activity_detail)
        setSupportActionBar(detail_toolbar)

        filmEvent = FilmEventDAO(applicationContext).get(intent.getStringExtra(FILM_EVENT_CODE))

        fab.setOnClickListener {
            RatingDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(DIALOG_TITLE, filmEvent?.title)
                    putString(DIALOG_WEBSITE, filmEvent?.website.toString())
                }
            }.show(fragmentManager, DIALOG_TAG)
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
            val fragment = DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(FILM_EVENT_CODE, intent.getStringExtra(FILM_EVENT_CODE))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back

            navigateUpTo(Intent(this, ListActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
