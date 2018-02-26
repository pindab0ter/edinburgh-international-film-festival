package nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives

import android.content.ContentValues
import android.media.Image
import com.google.gson.annotations.SerializedName
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDatabase
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.databaseStringToDate
import java.net.URL
import java.util.*

class FilmEvent() {
    constructor(code: String?, title: String?, description: String?, genreTags: Array<String>, website: URL?, imageOriginalUrl: URL?, imageThumbnailUrl: URL?, updated: Date?) : this() {
        this.code = code
        this.title = title
        this.description = description
        this.genreTags = genreTags
        this.website = website
        this.imageOriginalUrl = imageOriginalUrl
        this.imageThumbnailUrl = imageThumbnailUrl
        this.updated = updated
    }

    constructor(cv: ContentValues) : this() {
        this.code = cv.getAsString(FilmEventEntry.COLUMN_CODE)
        this.title = cv.getAsString(FilmEventEntry.COLUMN_TITLE)
        this.description = cv.getAsString(FilmEventEntry.COLUMN_DESCRIPTION)
        this.genreTagsSource = cv.getAsString(FilmEventEntry.COLUMN_GENRE_TAGS)
        this.website = URL(cv.getAsString(FilmEventEntry.COLUMN_WEBSITE))
        this.imageOriginalUrl = URL(cv.getAsString(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL))
        this.imageThumbnailUrl = URL(cv.getAsString(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL))
        this.updated = databaseStringToDate(cv.getAsString(FilmEventEntry.COLUMN_UPDATED))
    }

    var code: String? = null
    var description: String? = null

    @SerializedName("genre_tags")
    private var genreTagsSource: String? = null

    var genreTags: Array<String>?
        get() = genreTagsSource?.split(",")?.map { it.trim() }?.toTypedArray()
        set(value) {
            genreTagsSource = value?.joinToString()
        }

    enum class Status {
        @SerializedName("active")
        Active,
        @SerializedName("cancelled")
        Cancelled,
        @SerializedName("deleted")
        Deleted
    }

    var title: String? = null
    var updated: Date? = null
    var website: URL? = null

    var imageOriginal: Image? = null
    var imageOriginalUrl: URL?
        get() = images?.versions?.original?.url
        set(value) {
            images?.versions?.original?.url = value
        }

    var imageThumbnail: Image? = null
    var imageThumbnailUrl: URL?
        get() = images?.versions?.thumb100?.url
        set(value) {
            images?.versions?.thumb100?.url = value
        }

    open class Images {
        class Versions {
            class Version {
                var url: URL? = null
            }

            @SerializedName("original")
            var original: Version? = null
            @SerializedName("thumb-100")
            var thumb100: Version? = null
        }

        var versions: Versions? = null
    }

    // Subclass to prevent recursive deserialization
    class ImagesSubclass : Images()

    private var images: Images? = null

    class Performance() {
        constructor(cv: ContentValues) : this() {
            this.start = databaseStringToDate(cv.getAsString(PerformanceEntry.COLUMN_START))
            this.end = databaseStringToDate(cv.getAsString(PerformanceEntry.COLUMN_END))
        }

        constructor(start: Date?, end: Date?) : this() {
            this.start = start
            this.end = end
        }

        var start: Date? = null
        var end: Date? = null

        fun asContentValues(): ContentValues = ContentValues().apply {
            if (start != null) put(PerformanceEntry.COLUMN_START, start?.formatForDatabase())
            if (end != null) put(PerformanceEntry.COLUMN_START, end?.formatForDatabase())
        }
    }

    var performances: Array<Performance>? = null

    fun asContentValues(): ContentValues = ContentValues().apply {
        if (code != null) put(FilmEventEntry.COLUMN_CODE, code)
        if (title != null) put(FilmEventEntry.COLUMN_TITLE, title)
        if (description != null) put(FilmEventEntry.COLUMN_DESCRIPTION, description)
        if (genreTags != null) put(FilmEventEntry.COLUMN_GENRE_TAGS, genreTags?.joinToString())
        if (website != null) put(FilmEventEntry.COLUMN_WEBSITE, website?.toString())
        if (imageOriginalUrl != null) put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL, imageOriginalUrl?.toString())
        if (imageThumbnailUrl != null) put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL, imageThumbnailUrl?.toString())
        if (updated != null) put(FilmEventEntry.COLUMN_UPDATED, updated?.formatForDatabase())
    }
}