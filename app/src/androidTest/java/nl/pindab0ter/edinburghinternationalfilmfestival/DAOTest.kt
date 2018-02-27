package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.ContentResolver
import android.content.Context
import android.support.test.InstrumentationRegistry
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1
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
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent1WithPerformances
import nl.pindab0ter.edinburghinternationalfilmfestival.DataInstances.filmEvent2WithPerformances
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDbHelper
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.databaseStringToDate
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.net.URL

class DAOTest {
    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var filmEventDbHelper: FilmEventDbHelper
    private lateinit var filmEventDAO: FilmEventDAO

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        contentResolver = context.contentResolver
        filmEventDbHelper = FilmEventDbHelper(context)
        filmEventDAO = FilmEventDAO(context)

        filmEventDbHelper.dropAllTablesAndRecreate(filmEventDbHelper.writableDatabase)
    }

    @After
    fun tearDown() {
        filmEventDbHelper.dropAllTables(filmEventDbHelper.writableDatabase)
    }

    @Test
    fun insertFilmEvent() {
        filmEventDAO.insert(filmEvent1)

        contentResolver.query(FilmEventEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1Code).build(), null, null, null, null).apply {
            assertTrue("Expecting to find one film event entry", count == 1)

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
    }

    @Test
    fun insertFilmEventWithPerformances() {
        filmEventDAO.insert(filmEvent1WithPerformances)

        val filmEvent1Uri = FilmEventEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1Code).build()

        contentResolver.query(filmEvent1Uri, null, null, null, null).apply {
            assertTrue("Expecting to find one film event entry", count == 1)

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

        val filmEvent1PerformancesUri = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1Code).build()

        contentResolver.query(filmEvent1PerformancesUri, null, null, null).apply {
            Assert.assertTrue("Expecting to find two performance entries", count == 2)

            moveToFirst()
            Assert.assertEquals(1, getInt(getColumnIndex(FilmEventContract.PerformanceEntry.COLUMN_ID)))
            Assert.assertEquals(filmEvent1Code, getString(getColumnIndex(FilmEventContract.PerformanceEntry.COLUMN_FILM_EVENT_CODE)))
            Assert.assertEquals(filmEvent1Performance1Start, getString(getColumnIndex(FilmEventContract.PerformanceEntry.COLUMN_START)))
            Assert.assertEquals(filmEvent1Performance1End, getString(getColumnIndex(FilmEventContract.PerformanceEntry.COLUMN_END)))

            moveToNext()
            Assert.assertEquals(2, getInt(getColumnIndex(FilmEventContract.PerformanceEntry.COLUMN_ID)))
            Assert.assertEquals(filmEvent1Code, getString(getColumnIndex(FilmEventContract.PerformanceEntry.COLUMN_FILM_EVENT_CODE)))
            Assert.assertEquals(filmEvent1Performance2Start, getString(getColumnIndex(FilmEventContract.PerformanceEntry.COLUMN_START)))
            Assert.assertEquals(filmEvent1Performance2End, getString(getColumnIndex(FilmEventContract.PerformanceEntry.COLUMN_END)))
        }
    }

    @Test
    fun insertFilmEventArrayWithPerformances() {
        filmEventDAO.insert(listOf(filmEvent1WithPerformances, filmEvent2WithPerformances))

        contentResolver.query(FilmEventEntry.CONTENT_URI, null, null, null).apply {
            assertEquals("Expecting to find two film event entries", 2, count)
        }

        contentResolver.query(PerformanceEntry.CONTENT_URI, null, null, null).apply {
            assertEquals("Expecting to find four performance entries", 4, count)
        }
    }

    @Test
    fun getFilmEvent() {
        val filmEvent1Uri = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1ContentValues)

        val filmEvent = filmEventDAO.get(filmEvent1Uri.lastPathSegment)

        assertTrue(filmEvent != null)

        assertEquals(filmEvent1Code, filmEvent?.code)
        assertEquals(filmEvent1Title, filmEvent?.title)
        assertEquals(filmEvent1Description, filmEvent?.description)
        assertArrayEquals(filmEvent1GenreTags.split(",").map { it.trim() }.toTypedArray(), filmEvent?.genreTags)
        assertEquals(URL(filmEvent1Website), filmEvent?.website)
        assertEquals(databaseStringToDate(filmEvent1Updated), filmEvent?.updated)
        assertEquals(URL(filmEvent1ImgOrigUrl), filmEvent?.imageOriginalUrl)
        assertEquals(URL(filmEvent1ImgThumbUrl), filmEvent?.imageThumbnailUrl)
    }

    @Test
    fun getFilmEventWithPerformances() {
        val filmEvent1Uri = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1ContentValues)
        val filmEvent1PerformancesUri = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1Uri.lastPathSegment).build()
        contentResolver.insert(filmEvent1PerformancesUri, filmEvent1Performance1ContentValues)
        contentResolver.insert(filmEvent1PerformancesUri, filmEvent1Performance2ContentValues)

        val filmEvent = filmEventDAO.get(filmEvent1Uri.lastPathSegment)

        assertEquals(filmEvent1Code, filmEvent?.code)
        assertEquals(filmEvent1Title, filmEvent?.title)
        assertEquals(filmEvent1Description, filmEvent?.description)
        assertArrayEquals(filmEvent1GenreTags.split(",").map { it.trim() }.toTypedArray(), filmEvent?.genreTags)
        assertEquals(URL(filmEvent1Website), filmEvent?.website)
        assertEquals(databaseStringToDate(filmEvent1Updated), filmEvent?.updated)
        assertEquals(URL(filmEvent1ImgOrigUrl), filmEvent?.imageOriginalUrl)
        assertEquals(URL(filmEvent1ImgThumbUrl), filmEvent?.imageThumbnailUrl)

        assertEquals(databaseStringToDate(filmEvent1Performance1Start), filmEvent?.performances?.get(0)?.start)
        assertEquals(databaseStringToDate(filmEvent1Performance1End), filmEvent?.performances?.get(0)?.end)

        assertEquals(databaseStringToDate(filmEvent1Performance2Start), filmEvent?.performances?.get(1)?.start)
        assertEquals(databaseStringToDate(filmEvent1Performance2End), filmEvent?.performances?.get(1)?.end)
    }
}