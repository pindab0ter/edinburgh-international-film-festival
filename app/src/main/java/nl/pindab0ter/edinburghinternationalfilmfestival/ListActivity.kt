package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_master.*
import kotlinx.android.synthetic.main.film_list.*
import nl.pindab0ter.edinburghinternationalfilmfestival.R.layout.activity_master
import nl.pindab0ter.edinburghinternationalfilmfestival.R.menu.menu_list_activity
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsRequest
import nl.pindab0ter.edinburghinternationalfilmfestival.dummy.EDINBURGH_FILM_FESTIVAL_REPLY_FIRST
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.LongLog

class ListActivity : AppCompatActivity() {

    private val logTag = ListActivity::class.simpleName
    private val twoPane: Boolean // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
        get() = detail_container != null
    private lateinit var adapter: FilmEventsRecyclerViewAdapter
    private var genres: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_master)

        setSupportActionBar(toolbar)
        toolbar.title = title

        setupRecyclerView(film_list)
//        fetchFilmEvents()
        testDatabase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(menu_list_activity, menu)

        if (genres == null) return true

        menu?.findItem(R.id.filter_placeholder)?.isVisible = false
        menu?.findItem(R.id.filter_none)?.isVisible = true

        menu?.findItem(R.id.filter)?.subMenu?.apply {
            genres!!.forEachIndexed { index, genre ->
                add(R.id.filter_group, index, index, genre)
            }
            setGroupCheckable(R.id.filter_group, true, true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.groupId) {
            R.id.sort_group -> {
                adapter.sortBy(item.itemId)
                item.isChecked = true
            }
            R.id.filter_group -> {
                if (item.itemId == R.id.filter_placeholder) return false
                adapter.filterBy(item.title)
                item.isChecked = true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = FilmEventsRecyclerViewAdapter(this, twoPane)
        recyclerView.adapter = adapter
    }

    private fun testDatabase() {
        contentResolver.delete(FilmEventEntry.CONTENT_URI, null, null)
        contentResolver.delete(PerformanceEntry.CONTENT_URI, null, null)

        val filmEvent1 = ContentValues().apply {
            put(FilmEventEntry.COLUMN_CODE, "2104")
            put(FilmEventEntry.COLUMN_TITLE, "Kafka’s The Burrow (Kafka’s Der Bau)")
            put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL, "https://edfestimages.s3.amazonaws.com/3c/51/04/3c510480057151a1180c9af9f7664ee80cf57ab2-original.jpg")
            put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL, "https://edfestimages.s3.amazonaws.com/3c/51/04/3c510480057151a1180c9af9f7664ee80cf57ab2-thumb-100.png")
            put(FilmEventEntry.COLUMN_UPDATED, "2015-06-08 11:50:05")
        }

        val filmEvent1Id: Int? = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1).lastPathSegment.toInt()

        val performance1_1 = ContentValues().apply {
            put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1Id)
            put(PerformanceEntry.COLUMN_START, "2015-06-19 20:35:00")
            put(PerformanceEntry.COLUMN_END, "2015-06-19 22:25:00")
            put(PerformanceEntry.COLUMN_PRICE, 10)
        }

        val performance1_2 = ContentValues().apply {
            put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1Id)
            put(PerformanceEntry.COLUMN_START, "2015-06-23 18:05:00")
            put(PerformanceEntry.COLUMN_END, "2015-06-23 19:55:00")
            put(PerformanceEntry.COLUMN_PRICE, 10)
        }

        contentResolver.insert(PerformanceEntry.CONTENT_URI, performance1_1)
        contentResolver.insert(PerformanceEntry.CONTENT_URI, performance1_2)

        contentResolver.delete(FilmEventEntry.CONTENT_URI.buildUpon().appendPath("$filmEvent1Id").build(), null, null)

        /*val filmEvent2 = ContentValues().apply {
            put(FilmEventEntry.COLUMN_CODE, "2152")
            put(FilmEventEntry.COLUMN_TITLE, "45 Years")
            put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL, "https://edfestimages.s3.amazonaws.com/7a/b8/ba/7ab8bad13251a35f9957d5375d9c2a3945aa7642-original.jpg")
            put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL, "https://edfestimages.s3.amazonaws.com/7a/b8/ba/7ab8bad13251a35f9957d5375d9c2a3945aa7642-thumb-100.png")
            put(FilmEventEntry.COLUMN_UPDATED, "2015-06-25 12:50:05")
        }

        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent2)
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent2) // Force conflict*/

        contentResolver.query(PerformanceEntry.CONTENT_URI, null, null, null, null).apply {
            if (count > 0) {
                moveToFirst()
                do {
                    Log.v(logTag, """
                |       _id: ${getInt(getColumnIndex(PerformanceEntry.COLUMN_ID))}
                |event_code: ${getInt(getColumnIndex(PerformanceEntry.COLUMN_FILM_EVENT_CODE))}
                |     start: ${getString(getColumnIndex(PerformanceEntry.COLUMN_START))}
                |       end: ${getString(getColumnIndex(PerformanceEntry.COLUMN_END))}
                |     price: ${getInt(getColumnIndex(PerformanceEntry.COLUMN_PRICE))}
                """.trimMargin())
                } while (moveToNext())
                close()
            } else {
                Log.v(logTag, "No performance entries found")
            }
        }

        /*val querySingleUri = FilmEventEntry.CONTENT_URI.buildUpon().appendPath("$filmEvent1Id").build()
        contentResolver.query(querySingleUri, null, null, null, null).apply {
            if (count > 0) {
                moveToFirst()
                Log.v(logTag, """
                |   code: ${getString(getColumnIndex(FilmEventEntry.COLUMN_CODE))}
                |  title: ${getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE))}
                |updated: ${getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED))}
                |   orig: ${getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL))}
                |  thumb: ${getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL))}
                """.trimMargin())

                val queryPerformancesByFilmEventId = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(getInt(getColumnIndex(FilmEventEntry.COLUMN_CODE)).toString()).build()
                contentResolver.query(queryPerformancesByFilmEventId, null, null, null, null).apply {
                    if (count > 0) {
                        moveToFirst()
                        do {
                            Log.v(logTag, """
                            |     _id: ${getInt(getColumnIndex(PerformanceEntry.COLUMN_ID))}
                            |event_id: ${getInt(getColumnIndex(PerformanceEntry.COLUMN_FILM_EVENT_CODE))}
                            |   start: ${getString(getColumnIndex(PerformanceEntry.COLUMN_START))}
                            |     end: ${getString(getColumnIndex(PerformanceEntry.COLUMN_END))}
                            |   price: ${getInt(getColumnIndex(PerformanceEntry.COLUMN_PRICE))}
                            """.trimMargin())
                        } while (moveToNext())
                    } else {
                        Log.v(logTag, "No result for $queryPerformancesByFilmEventId")
                    }
                }
            } else {
                Log.v(logTag, "No result for $querySingleUri")
            }
            close()
        }*/
    }

    private fun fetchFilmEvents() = FilmEventsFetcher(this, { filmEvents ->
        adapter.swapFilmEvents(filmEvents)
        genres = filmEvents.mapNotNull { it.genreTags?.asIterable() }.flatten().distinct().sorted()

        LongLog.d(logTag, EDINBURGH_FILM_FESTIVAL_REPLY_FIRST)
        LongLog.d(logTag, FilmEventsRequest.gson.toJson(filmEvents.first()))

        invalidateOptionsMenu()
    }).fetchOffline()
}
