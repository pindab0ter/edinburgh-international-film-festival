package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_film_detail.*
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmDetailFragment.Companion.ARG_FILM_DESCRIPTION
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmDetailFragment.Companion.ARG_FILM_IMAGE_URL
import nl.pindab0ter.edinburghinternationalfilmfestival.FilmDetailFragment.Companion.ARG_FILM_TITLE

/**
 * An activity representing a single FilmEvent detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [FilmListActivity].
 */
class FilmDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_detail)
        setSupportActionBar(detail_toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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
            val fragment = FilmDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FILM_TITLE, intent.getStringExtra(ARG_FILM_TITLE))
                    putString(ARG_FILM_DESCRIPTION, intent.getStringExtra(ARG_FILM_DESCRIPTION))
                    putString(ARG_FILM_IMAGE_URL, intent.getStringExtra(FilmDetailFragment.ARG_FILM_IMAGE_URL))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.film_detail_container, fragment)
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

            navigateUpTo(Intent(this, FilmListActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
