package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.net.Uri
import android.provider.BaseColumns

object FilmEventsContract {
    const val CONTENT_AUTHORITY = "nl.pindab0ter.edinburghinternationalfilmfestival"
    const val PATH_FILM_EVENTS = "film_events"
    const val PATH_FILM_EVENT_BY_ID = PATH_FILM_EVENTS + "/#"
    const val PATH_PERFORMANCES = "performances"
    const val PATH_PERFORMANCE_BY_FILM_EVENT_ID = PATH_PERFORMANCES + "/#"

    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")!!

    object FilmEventEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILM_EVENTS).build()!!
        const val TABLE_NAME = "film_events"

        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_CODE = "code"
        const val COLUMN_TITLE = "title"
        const val COLUMN_IMAGE_THUMBNAIL = "image_thumb"
        const val COLUMN_IMAGE_THUMBNAIL_URL = "image_thumb_url"
        const val COLUMN_IMAGE_ORIGINAL = "image_orig"
        const val COLUMN_IMAGE_ORIGINAL_URL = "image_orig_url"
        const val COLUMN_UPDATED = "updated"

        /*
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_GENRE = "genre"
        const val COLUMN_GENRE_TAGS = "genre_tags"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_STATUS = "status"
        const val COLUMN_URL = "url"
        const val COLUMN_WEBSITE = "website"
        const val COLUMN_YEAR = "year"
        const val COLUMN_IMAGES = "images"
        const val COLUMN_VENUE = "venue"
        */
    }

    object PerformanceEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERFORMANCES).build()!!
        const val TABLE_NAME = "performances"

        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_FILM_EVENT_ID = "film_event_id"
        const val COLUMN_START = "start"
        const val COLUMN_END = "end"
        const val COLUMN_PRICE = "price"
    }

    /*
    object ImagesEntry : BaseColumns {
        const val _ID = BaseColumns.COLUMN_ID
        const val COLUMN_ORIENTATION = "orientation"
        const val COLUMN_TYPE = "type"
        const val COLUMN_VERSIONS = "versions"
    }

    object VersionsEntry : BaseColumns {
        const val _ID = BaseColumns.COLUMN_ID
        const val COLUMN_ORIGINAL = "original"
        const val COLUMN_SQUARE75 = "square_75"
        const val COLUMN_SQUARE150 = "square_150"
        const val COLUMN_THUMB100 = "thumb_100"
        const val COLUMN_MEDIUM640 = "medium_640"
        const val COLUMN_SMALL320 = "small_320"
        const val COLUMN_LARGE1024 = "large_1024"
    }

    object VersionEntry : BaseColumns {
        const val _ID = BaseColumns.COLUMN_ID
        const val COLUMN_TYPE = "type"
        const val COLUMN_MIME = "mime"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_WIDTH = "width"
        const val COLUMN_URL = "url"
    }

    object VenueEntry : BaseColumns {
        const val _ID = BaseColumns.COLUMN_ID
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_CODE = "code"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_POST_CODE = "post_code"
        const val COLUMN_WEB_ADDRESS = "web_address"
    }
    */
}