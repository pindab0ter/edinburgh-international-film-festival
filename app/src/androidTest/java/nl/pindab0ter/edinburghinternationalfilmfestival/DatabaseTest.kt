package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.ContentResolver
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Code
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1ContentValues
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Description
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1GenreTags
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1ImgOrigUrl
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1ImgThumbUrl
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Performance1ContentValues
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Performance1End
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Performance1Start
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Performance2ContentValues
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Performance2End
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Performance2Start
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Title
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Updated
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1Website
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent2Code
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent2ContentValues
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent2Performance1ContentValues
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent2Performance2ContentValues
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDbHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var context: Context
    private lateinit var filmEventDbHelper: FilmEventDbHelper
    private lateinit var contentResolver: ContentResolver

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        filmEventDbHelper = FilmEventDbHelper(context)
        contentResolver = context.contentResolver

        filmEventDbHelper.dropAllTablesAndRecreate(filmEventDbHelper.writableDatabase)
    }

    @After
    fun tearDown() {
        filmEventDbHelper.dropAllTables(filmEventDbHelper.writableDatabase)
    }

    @Test
    fun insertAndQuery() {
        // Insert filmEvent1ContentValues
        val filmEvent1Uri = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1ContentValues)
        val filmEvent1PerformancesUri = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1Uri.lastPathSegment).build()

        // Insert performances
        contentResolver.insert(filmEvent1PerformancesUri, filmEvent1Performance1ContentValues)
        contentResolver.insert(filmEvent1PerformancesUri, filmEvent1Performance2ContentValues)

        // Insert filmEvent2ContentValues
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent2ContentValues)

        contentResolver.query(filmEvent1Uri, null, null, null, null).apply {
            assertEquals("Expecting to find one film event entry", 1, count)

            moveToFirst()
            assertEquals(filmEvent1Code, getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)))
            assertEquals(filmEvent1Title, getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE)))
            assertEquals(filmEvent1Description, getString(getColumnIndex(FilmEventEntry.COLUMN_DESCRIPTION)))
            assertEquals(filmEvent1GenreTags, getString(getColumnIndex(FilmEventEntry.COLUMN_GENRE_TAGS)))
            assertEquals(filmEvent1Website, getString(getColumnIndex(FilmEventEntry.COLUMN_WEBSITE)))
            assertEquals(filmEvent1Updated, getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED)))
            assertEquals(filmEvent1ImgOrigUrl, getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL)))
            assertEquals(filmEvent1ImgThumbUrl, getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL)))

            close()
        }

        contentResolver.query(FilmEventEntry.CONTENT_URI, null, null, null, null).apply {
            assertEquals("Expecting to find two film event entries", 2, count)

            moveToFirst()
            assertEquals(filmEvent1Code, getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)))

            moveToNext()
            assertEquals(filmEvent2Code, getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)))

            close()
        }

        contentResolver.query(filmEvent1PerformancesUri, null, null, null).apply {
            assertEquals("Expecting to find two performance entries", 2, count)

            moveToFirst()
            assertEquals(1, getInt(getColumnIndex(PerformanceEntry.COLUMN_ID)))
            assertEquals(filmEvent1Performance1Start, getString(getColumnIndex(PerformanceEntry.COLUMN_START)))
            assertEquals(filmEvent1Performance1End, getString(getColumnIndex(PerformanceEntry.COLUMN_END)))

            moveToNext()
            assertEquals(2, getInt(getColumnIndex(PerformanceEntry.COLUMN_ID)))
            assertEquals(filmEvent1Performance2Start, getString(getColumnIndex(PerformanceEntry.COLUMN_START)))
            assertEquals(filmEvent1Performance2End, getString(getColumnIndex(PerformanceEntry.COLUMN_END)))
        }
    }

    @Test
    fun primaryKey() {
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1ContentValues)
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1ContentValues)

        contentResolver.query(FilmEventEntry.CONTENT_URI, null, null, null).apply {
            assertEquals("Expecting to find one performance entry", 1, count)
        }
    }

    @Test
    fun bulkInsertFilmEntries() {
        contentResolver.bulkInsert(FilmEventEntry.CONTENT_URI, arrayOf(filmEvent1ContentValues, filmEvent2ContentValues))

        contentResolver.query(FilmEventEntry.CONTENT_URI, null, null, null).apply {
            assertEquals("Expecting to find two film event entries", 2, count)
        }

        val performance1Url = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1Code).build()
        contentResolver.bulkInsert(performance1Url, arrayOf(filmEvent1Performance1ContentValues, filmEvent1Performance2ContentValues))

        val performance2Url = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent2Code).build()
        contentResolver.bulkInsert(performance2Url, arrayOf(filmEvent2Performance1ContentValues, filmEvent2Performance2ContentValues))

        contentResolver.query(PerformanceEntry.CONTENT_URI, null, null, null).apply {
            assertEquals("Expecting to find four performance entries", 4, count)
        }
    }

    @Test
    fun foreignKey() {
        // Insert filmEvent1ContentValues
        val filmEvent1Uri = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1ContentValues)
        val filmEvent1PerformancesUri = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1Uri.lastPathSegment).build()

        // Insert performances
        contentResolver.insert(filmEvent1PerformancesUri, filmEvent1Performance1ContentValues)
        contentResolver.insert(filmEvent1PerformancesUri, filmEvent1Performance2ContentValues)

        // Delete filmEvent 1
        contentResolver.delete(filmEvent1Uri, null, null)

        contentResolver.query(filmEvent1PerformancesUri, null, null, null).apply {
            assertEquals("Expecting to find no performance entries", 0, count)
        }
    }
}
