package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsDbHelper
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var context: Context
    private lateinit var filmEventsDbHelper: FilmEventsDbHelper
    private lateinit var contentResolver: ContentResolver

    companion object {
        private const val filmEvent1Code = "2104"
        private const val filmEvent1Title = "Kafka’s The Burrow (Kafka’s Der Bau)"
        private const val filmEvent1ImgOrigUrl = "https://edfestimages.s3.amazonaws.com/3c/51/04/3c510480057151a1180c9af9f7664ee80cf57ab2-original.jpg"
        private const val filmEvent1ImgThumbUrl = "https://edfestimages.s3.amazonaws.com/3c/51/04/3c510480057151a1180c9af9f7664ee80cf57ab2-thumb-100.png"
        private const val filmEvent1Updated = "2015-06-08 11:50:05"

        private val filmEvent1 = ContentValues().apply {
            put(FilmEventEntry.COLUMN_CODE, filmEvent1Code)
            put(FilmEventEntry.COLUMN_TITLE, filmEvent1Title)
            put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL, filmEvent1ImgOrigUrl)
            put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL, filmEvent1ImgThumbUrl)
            put(FilmEventEntry.COLUMN_UPDATED, filmEvent1Updated)
        }

        private const val filmEvent1Performance1Start = "2015-06-19 20:35:00"
        private const val filmEvent1Performance1End = "2015-06-19 22:25:00"
        private const val filmEvent1Performance1Price = 10

        private val filmEvent1Performance1 = ContentValues().apply {
            put(PerformanceEntry.COLUMN_START, filmEvent1Performance1Start)
            put(PerformanceEntry.COLUMN_END, filmEvent1Performance1End)
            put(PerformanceEntry.COLUMN_PRICE, filmEvent1Performance1Price)
        }

        private const val filmEvent1Performance2Start = "2015-06-23 18:05:00"
        private const val filmEvent1Performance2End = "2015-06-23 19:55:00"
        private const val filmEvent1Performance2Price = 10

        private val filmEvent1Performance2 = ContentValues().apply {
            put(PerformanceEntry.COLUMN_START, filmEvent1Performance2Start)
            put(PerformanceEntry.COLUMN_END, filmEvent1Performance2End)
            put(PerformanceEntry.COLUMN_PRICE, filmEvent1Performance2Price)
        }

        private const val filmEvent2Code = "2152"
        private const val filmEvent2Title = "45 Years"
        private const val filmEvent2ImgOrigUrl = "https://edfestimages.s3.amazonaws.com/7a/b8/ba/7ab8bad13251a35f9957d5375d9c2a3945aa7642-original.jpg"
        private const val filmEvent2ImgThumbUrl = "https://edfestimages.s3.amazonaws.com/7a/b8/ba/7ab8bad13251a35f9957d5375d9c2a3945aa7642-thumb-100.png"
        private const val filmEvent2Updated = "2015-06-25 12:50:05"

        private val filmEvent2 = ContentValues().apply {
            put(FilmEventEntry.COLUMN_CODE, filmEvent2Code)
            put(FilmEventEntry.COLUMN_TITLE, filmEvent2Title)
            put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL, filmEvent2ImgOrigUrl)
            put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL, filmEvent2ImgThumbUrl)
            put(FilmEventEntry.COLUMN_UPDATED, filmEvent2Updated)
        }
    }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        filmEventsDbHelper = FilmEventsDbHelper(context)
        contentResolver = context.contentResolver

        filmEventsDbHelper.dropAllTablesAndRecreate(filmEventsDbHelper.writableDatabase)
    }

    @After
    fun tearDown() {
        filmEventsDbHelper.dropAllTables(filmEventsDbHelper.writableDatabase)
    }

    @Test
    fun insertAndQuery() {
        val filmEvent1Uri = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1)
        val filmEvent1CodeFromUri = filmEvent1Uri.lastPathSegment

        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent2)

        filmEvent1Performance1.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1CodeFromUri)
        filmEvent1Performance2.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1CodeFromUri)

        contentResolver.insert(PerformanceEntry.CONTENT_URI, filmEvent1Performance1)
        contentResolver.insert(PerformanceEntry.CONTENT_URI, filmEvent1Performance2)

        contentResolver.query(filmEvent1Uri, null, null, null, null).apply {
            assertTrue("Expecting to find one film event entry", count == 1)

            moveToFirst()
            assertEquals(filmEvent1Code, getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)))
            assertEquals(filmEvent1Title, getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE)))
            assertEquals(filmEvent1Updated, getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED)))
            assertEquals(filmEvent1ImgOrigUrl, getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL)))
            assertEquals(filmEvent1ImgThumbUrl, getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL)))

            close()
        }

        contentResolver.query(FilmEventEntry.CONTENT_URI, null, null, null, null).apply {
            assertTrue("Expecting to find two film event entries", count == 2)

            moveToFirst()
            assertEquals(filmEvent1Code, getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)))
            assertEquals(filmEvent1Title, getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE)))
            assertEquals(filmEvent1Updated, getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED)))
            assertEquals(filmEvent1ImgOrigUrl, getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL)))
            assertEquals(filmEvent1ImgThumbUrl, getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL)))

            moveToNext()
            assertEquals(filmEvent2Code, getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)))
            assertEquals(filmEvent2Title, getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE)))
            assertEquals(filmEvent2Updated, getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED)))
            assertEquals(filmEvent2ImgOrigUrl, getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL)))
            assertEquals(filmEvent2ImgThumbUrl, getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL)))

            close()
        }

        val filmEvent1PerformancesUri = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1CodeFromUri).build()

        contentResolver.query(filmEvent1PerformancesUri, null, null, null).apply {
            assertTrue("Expecting to find two performance entries", count == 2)

            moveToFirst()
            assertEquals(1, getInt(getColumnIndex(PerformanceEntry.COLUMN_ID)))
            assertEquals(filmEvent1CodeFromUri, getString(getColumnIndex(PerformanceEntry.COLUMN_FILM_EVENT_CODE)))
            assertEquals(filmEvent1Performance1Start, getString(getColumnIndex(PerformanceEntry.COLUMN_START)))
            assertEquals(filmEvent1Performance1End, getString(getColumnIndex(PerformanceEntry.COLUMN_END)))
            assertEquals(filmEvent1Performance1Price, getInt(getColumnIndex(PerformanceEntry.COLUMN_PRICE)))

            moveToNext()
            assertEquals(2, getInt(getColumnIndex(PerformanceEntry.COLUMN_ID)))
            assertEquals(filmEvent1CodeFromUri, getString(getColumnIndex(PerformanceEntry.COLUMN_FILM_EVENT_CODE)))
            assertEquals(filmEvent1Performance2Start, getString(getColumnIndex(PerformanceEntry.COLUMN_START)))
            assertEquals(filmEvent1Performance2End, getString(getColumnIndex(PerformanceEntry.COLUMN_END)))
            assertEquals(filmEvent1Performance2Price, getInt(getColumnIndex(PerformanceEntry.COLUMN_PRICE)))
        }
    }

    @Test
    fun primaryKey() {
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1)
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1)

        contentResolver.query(FilmEventEntry.CONTENT_URI, null, null, null).apply {
            assertTrue("Expecting to find two performance entries", count == 1)
        }
    }

    @Test
    fun foreignKey() {
        val filmEvent1Uri = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1)
        val filmEvent1CodeFromUri = filmEvent1Uri.lastPathSegment

        filmEvent1Performance1.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1CodeFromUri)
        filmEvent1Performance2.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1CodeFromUri)

        contentResolver.insert(PerformanceEntry.CONTENT_URI, filmEvent1Performance1)
        contentResolver.insert(PerformanceEntry.CONTENT_URI, filmEvent1Performance2)

        contentResolver.delete(filmEvent1Uri, null, null)

        val filmEvent1PerformancesUri = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1CodeFromUri).build()

        contentResolver.query(filmEvent1PerformancesUri, null, null, null).apply {
            assertTrue("Expecting to find no performance entries", count == 0)
        }
    }
}
