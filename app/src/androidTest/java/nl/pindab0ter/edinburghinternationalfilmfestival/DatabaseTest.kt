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
        // Insert filmEvent1
        val filmEvent1Uri = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1)
        val filmEvent1CodeFromUri = filmEvent1Uri.lastPathSegment

        // Add filmEvent1's PK to the performances
        filmEvent1Performance1.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1CodeFromUri)
        filmEvent1Performance2.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1CodeFromUri)

        // Insert performances
        contentResolver.insert(PerformanceEntry.CONTENT_URI, filmEvent1Performance1)
        contentResolver.insert(PerformanceEntry.CONTENT_URI, filmEvent1Performance2)

        // Insert filmEvent2
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent2)

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
        // Insert filmEvent1
        val filmEvent1Uri = contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent1)
        val filmEvent1CodeFromUri = filmEvent1Uri.lastPathSegment

        // Add filmEvent1's PK to the performances
        filmEvent1Performance1.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1CodeFromUri)
        filmEvent1Performance2.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEvent1CodeFromUri)

        // Insert performances
        contentResolver.insert(PerformanceEntry.CONTENT_URI, filmEvent1Performance1)
        contentResolver.insert(PerformanceEntry.CONTENT_URI, filmEvent1Performance2)

        // Delete filmEvent 1
        contentResolver.delete(filmEvent1Uri, null, null)

        val filmEvent1PerformancesUri = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent1CodeFromUri).build()

        contentResolver.query(filmEvent1PerformancesUri, null, null, null).apply {
            assertTrue("Expecting to find no performance entries", count == 0)
        }
    }

    companion object {

        //
        // FILM EVENT 1
        //
        private const val filmEvent1Code = "2104"
        private const val filmEvent1Title = "Kafka’s The Burrow (Kafka’s Der Bau)"
        private const val filmEvent1Description = "<p>Adapted from Kafka's 1923 short story&nbsp;<em>Der Bau</em>, and using much of the author's original language, this is a film about loneliness, paranoia and abject isolation. A seemingly successful business man moves with his beautiful family into a shiny, new apartment block, but as the building starts to disintegrate around him and the terrifying dangers of the outside world threaten to encroach, it soon becomes clear that with this family, all is not as it first appeared.</p>\n<p><img src=\"/uploads/germanfilms.jpg\" width=\"200\" height=\"62\" alt=\"\" /></p>"
        private const val filmEvent1GenreTags = "Feature, New Perspectives"
        private const val filmEvent1Website = "http://www.edfilmfest.org.uk/films/2015/kafkas-the-burrow"
        private const val filmEvent1ImgOrigUrl = "https://edfestimages.s3.amazonaws.com/3c/51/04/3c510480057151a1180c9af9f7664ee80cf57ab2-original.jpg"
        private const val filmEvent1ImgThumbUrl = "https://edfestimages.s3.amazonaws.com/3c/51/04/3c510480057151a1180c9af9f7664ee80cf57ab2-thumb-100.png"
        private const val filmEvent1Updated = "2015-06-08 11:50:05"

        private val filmEvent1 = ContentValues().apply {
            put(FilmEventEntry.COLUMN_CODE, filmEvent1Code)
            put(FilmEventEntry.COLUMN_TITLE, filmEvent1Title)
            put(FilmEventEntry.COLUMN_DESCRIPTION, filmEvent1Description)
            put(FilmEventEntry.COLUMN_GENRE_TAGS, filmEvent1GenreTags)
            put(FilmEventEntry.COLUMN_WEBSITE, filmEvent1Website)
            put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL, filmEvent1ImgOrigUrl)
            put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL, filmEvent1ImgThumbUrl)
            put(FilmEventEntry.COLUMN_UPDATED, filmEvent1Updated)
        }

        //
        // FILM EVENT 1, PERFORMANCE 1
        //
        private const val filmEvent1Performance1Start = "2015-06-19 20:35:00"
        private const val filmEvent1Performance1End = "2015-06-19 22:25:00"
        private const val filmEvent1Performance1Price = 10

        private val filmEvent1Performance1 = ContentValues().apply {
            put(PerformanceEntry.COLUMN_START, filmEvent1Performance1Start)
            put(PerformanceEntry.COLUMN_END, filmEvent1Performance1End)
            put(PerformanceEntry.COLUMN_PRICE, filmEvent1Performance1Price)
        }

        //
        // FILM EVENT 1, PERFORMANCE 2
        //
        private const val filmEvent1Performance2Start = "2015-06-23 18:05:00"
        private const val filmEvent1Performance2End = "2015-06-23 19:55:00"
        private const val filmEvent1Performance2Price = 10

        private val filmEvent1Performance2 = ContentValues().apply {
            put(PerformanceEntry.COLUMN_START, filmEvent1Performance2Start)
            put(PerformanceEntry.COLUMN_END, filmEvent1Performance2End)
            put(PerformanceEntry.COLUMN_PRICE, filmEvent1Performance2Price)
        }

        //
        // FILM EVENT 2
        //
        private const val filmEvent2Code = "2152"
        private const val filmEvent2Title = "45 Years"
        private const val filmEvent2Description = "<p>One of the best British films of the year, Andrew Haigh&rsquo;s subtly devastating film is about the fractured relationship between a couple - delightfully played by Charlotte Rampling and Tom Courtenay &ndash; as they head towards their 45th wedding anniversary party. A powerful and enthralling follow-up to Haigh&rsquo;s critically acclaimed 2011 film <em>Weekend</em>, the film (a success at this year&rsquo;s Berlin Film Festival) dwells on how an apparently happy union can be tormented by an echo from the past, with a decades-long bond threatened by insecurities and irrational jealousy.</p>"
        private const val filmEvent2GenreTags = "Feature, Best of British"
        private const val filmEvent2Website = "http://www.edfilmfest.org.uk/films/2015/45-years"
        private const val filmEvent2ImgOrigUrl = "https://edfestimages.s3.amazonaws.com/7a/b8/ba/7ab8bad13251a35f9957d5375d9c2a3945aa7642-original.jpg"
        private const val filmEvent2ImgThumbUrl = "https://edfestimages.s3.amazonaws.com/7a/b8/ba/7ab8bad13251a35f9957d5375d9c2a3945aa7642-thumb-100.png"
        private const val filmEvent2Updated = "2015-06-25 12:50:05"

        private val filmEvent2 = ContentValues().apply {
            put(FilmEventEntry.COLUMN_CODE, filmEvent2Code)
            put(FilmEventEntry.COLUMN_TITLE, filmEvent2Title)
            put(FilmEventEntry.COLUMN_DESCRIPTION, filmEvent2Description)
            put(FilmEventEntry.COLUMN_GENRE_TAGS, filmEvent2GenreTags)
            put(FilmEventEntry.COLUMN_WEBSITE, filmEvent2Website)
            put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL, filmEvent2ImgOrigUrl)
            put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL, filmEvent2ImgThumbUrl)
            put(FilmEventEntry.COLUMN_UPDATED, filmEvent2Updated)
        }
    }
}
