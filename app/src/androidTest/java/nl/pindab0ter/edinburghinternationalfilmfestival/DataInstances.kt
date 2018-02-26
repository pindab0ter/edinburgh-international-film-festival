package nl.pindab0ter.edinburghinternationalfilmfestival

import android.content.ContentValues
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

object DataInstances {

    //
    // FILM EVENT 1
    //
    const val filmEvent1Code = "2104"
    const val filmEvent1Title = "Kafka’s The Burrow (Kafka’s Der Bau)"
    const val filmEvent1Description = "<p>Adapted from Kafka's 1923 short story&nbsp;<em>Der Bau</em>, and using much of the author's original language, this is a film about loneliness, paranoia and abject isolation. A seemingly successful business man moves with his beautiful family into a shiny, new apartment block, but as the building starts to disintegrate around him and the terrifying dangers of the outside world threaten to encroach, it soon becomes clear that with this family, all is not as it first appeared.</p>\n<p><img src=\"/uploads/germanfilms.jpg\" width=\"200\" height=\"62\" alt=\"\" /></p>"
    const val filmEvent1GenreTags = "Feature, New Perspectives"
    const val filmEvent1Website = "http://www.edfilmfest.org.uk/films/2015/kafkas-the-burrow"
    const val filmEvent1ImgOrigUrl = "https://edfestimages.s3.amazonaws.com/3c/51/04/3c510480057151a1180c9af9f7664ee80cf57ab2-original.jpg"
    const val filmEvent1ImgThumbUrl = "https://edfestimages.s3.amazonaws.com/3c/51/04/3c510480057151a1180c9af9f7664ee80cf57ab2-thumb-100.png"
    const val filmEvent1Updated = "2015-06-08 11:50:05"

    val filmEvent1 = FilmEvent()

    val filmEvent1ContentValues = ContentValues().apply {
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
    const val filmEvent1Performance1Start = "2015-06-19 20:35:00"
    const val filmEvent1Performance1End = "2015-06-19 22:25:00"

    val filmEvent1Performance1 = ContentValues().apply {
        put(PerformanceEntry.COLUMN_START, filmEvent1Performance1Start)
        put(PerformanceEntry.COLUMN_END, filmEvent1Performance1End)
    }

    //
    // FILM EVENT 1, PERFORMANCE 2
    //
    const val filmEvent1Performance2Start = "2015-06-23 18:05:00"
    const val filmEvent1Performance2End = "2015-06-23 19:55:00"

    val filmEvent1Performance2 = ContentValues().apply {
        put(PerformanceEntry.COLUMN_START, filmEvent1Performance2Start)
        put(PerformanceEntry.COLUMN_END, filmEvent1Performance2End)
    }

    //
    // FILM EVENT 2
    //
    const val filmEvent2Code = "2152"
    const val filmEvent2Title = "45 Years"
    const val filmEvent2Description = "<p>One of the best British films of the year, Andrew Haigh&rsquo;s subtly devastating film is about the fractured relationship between a couple - delightfully played by Charlotte Rampling and Tom Courtenay &ndash; as they head towards their 45th wedding anniversary party. A powerful and enthralling follow-up to Haigh&rsquo;s critically acclaimed 2011 film <em>Weekend</em>, the film (a success at this year&rsquo;s Berlin Film Festival) dwells on how an apparently happy union can be tormented by an echo from the past, with a decades-long bond threatened by insecurities and irrational jealousy.</p>"
    const val filmEvent2GenreTags = "Feature, Best of British"
    const val filmEvent2Website = "http://www.edfilmfest.org.uk/films/2015/45-years"
    const val filmEvent2ImgOrigUrl = "https://edfestimages.s3.amazonaws.com/7a/b8/ba/7ab8bad13251a35f9957d5375d9c2a3945aa7642-original.jpg"
    const val filmEvent2ImgThumbUrl = "https://edfestimages.s3.amazonaws.com/7a/b8/ba/7ab8bad13251a35f9957d5375d9c2a3945aa7642-thumb-100.png"
    const val filmEvent2Updated = "2015-06-25 12:50:05"

    val filmEvent2 = ContentValues().apply {
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