package nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives

import android.content.ContentValues
import android.net.Uri
import com.google.gson.annotations.SerializedName
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.databaseStringToDate
import java.util.*

class FilmEvent() : Observable() {
    constructor(code: String?, title: String?, description: String?, genreTags: String, website: String?, imageOriginalUrl: String?, imageThumbnailUrl: String?, updated: String?) : this() {
        this.code = code
        this.title = title
        this.description = description
        this._genreTags = genreTags
        this.website = Uri.parse(website)
        this.imageOriginal = Uri.parse(imageOriginalUrl)
        this.imageThumbnail = Uri.parse(imageThumbnailUrl)
        this.updated = databaseStringToDate(updated)
    }

    constructor(cv: ContentValues) : this() {
        this.code = cv.getAsString(FilmEventEntry.COLUMN_CODE)
        this.title = cv.getAsString(FilmEventEntry.COLUMN_TITLE)
        this.description = cv.getAsString(FilmEventEntry.COLUMN_DESCRIPTION)
        this._genreTags = cv.getAsString(FilmEventEntry.COLUMN_GENRE_TAGS)
        this.website = Uri.parse(cv.getAsString(FilmEventEntry.COLUMN_WEBSITE))
        this.imageOriginal = Uri.parse(cv.getAsString(FilmEventEntry.COLUMN_IMAGE_ORIGINAL))
        this.imageThumbnail = Uri.parse(cv.getAsString(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL))
        this.updated = databaseStringToDate(cv.getAsString(FilmEventEntry.COLUMN_UPDATED))
    }

    var code: String? = null
    var description: String? = null

    @SerializedName("genre_tags")
    var _genreTags: String? = null

    var genreTags: Array<String>?
        get() = _genreTags?.split(",")?.map { it.trim() }?.toTypedArray()
        set(value) {
            _genreTags = value?.joinToString()
        }

    var title: String? = null
    var updated: Date? = null
    var website: Uri? = null

    var imageOriginal: Uri?
        get() = _images?.versions?.original?.url
        set(value) {
            _images?.versions?.original?.url = value
        }

    var imageThumbnail: Uri?
        get() = _images?.versions?.thumb100?.url
        set(value) {
            _images?.versions?.thumb100?.url = value
        }

    open class Images {
        class Versions {
            class Version {
                var url: Uri? = null
            }

            @SerializedName("original")
            var original: Version? = null
            @SerializedName("thumb-100")
            var thumb100: Version? = null

            init {
                original = Version()
                thumb100 = Version()
            }
        }

        var versions: Versions? = null

        init {
            this.versions = Versions()
        }
    }

    // Subclass to prevent recursive deserialization
    class ImagesSubclass : Images()

    @SerializedName("images")
    private var _images: Images? = null

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
    }

    var performances: Array<Performance>? = null

    init {
        _images = Images()
    }
}