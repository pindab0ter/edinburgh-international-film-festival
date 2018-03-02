package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.net.Uri
import android.provider.BaseColumns

object FilmEventContract {
    const val CONTENT_AUTHORITY = "nl.pindab0ter.edinburghinternationalfilmfestival"
    const val PATH_FILM_EVENTS = "film_event"
    const val PATH_FILM_EVENT_BY_CODE = PATH_FILM_EVENTS
    const val PATH_PERFORMANCES = "performance"
    const val PATH_PERFORMANCES_BY_ID = PATH_PERFORMANCES
    const val PATH_PERFORMANCES_BY_FILM_EVENT_CODE = "$PATH_FILM_EVENTS/$PATH_PERFORMANCES"

    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")!!

    object FilmEventEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILM_EVENTS).build()!!
        val BY_CODE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILM_EVENT_BY_CODE).build()!!
        const val TABLE_NAME = "film_event"

        const val COLUMN_CODE = "code"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_GENRE_TAGS = "genre_tags"
        const val COLUMN_WEBSITE = "website"
        const val COLUMN_IMAGE_ORIGINAL = "image_orig"
        const val COLUMN_IMAGE_THUMBNAIL = "image_thumb"
        const val COLUMN_UPDATED = "updated"
    }

    object PerformanceEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERFORMANCES).build()!!
        val BY_ID_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERFORMANCES_BY_ID).build()!!
        val BY_FILM_EVENT_CODE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILM_EVENTS).appendPath(PATH_PERFORMANCES).build()!!
        const val TABLE_NAME = "performance"

        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_FILM_EVENT_CODE = "film_event_code"
        const val COLUMN_START = "start"
        const val COLUMN_END = "end"
        const val COLUMN_SCHEDULED = "scheduled"
    }
}