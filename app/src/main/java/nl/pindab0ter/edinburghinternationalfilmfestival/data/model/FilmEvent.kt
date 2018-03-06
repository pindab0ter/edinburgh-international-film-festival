package nl.pindab0ter.edinburghinternationalfilmfestival.data.model

import android.content.ContentValues
import android.net.Uri
import com.google.gson.annotations.SerializedName
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.databaseStringToDate
import java.util.*

class FilmEvent() : Observable() {
    constructor(code: String?, title: String?, description: String?, genreTags: String?, website: String?, imageOriginal: String?, imageThumbnail: String?, updated: String?) : this() {
        this.code = code
        this.title = title
        this.description = description
        this._genreTags = genreTags
        this.website = Uri.parse(website)
        this.imageOriginal = Uri.parse(imageOriginal)
        this.imageThumbnail = Uri.parse(imageThumbnail)
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
            this.id = cv.getAsInteger(PerformanceEntry.COLUMN_ID)
            this.start = databaseStringToDate(cv.getAsString(PerformanceEntry.COLUMN_START))
            this.end = databaseStringToDate(cv.getAsString(PerformanceEntry.COLUMN_END))
            this.scheduled = cv.getAsInteger(PerformanceEntry.COLUMN_SCHEDULED) == 1
        }

        constructor(id: Int?, start: Date?, end: Date?, scheduled: Boolean) : this() {
            this.id = id
            this.start = start
            this.end = end
            this.scheduled = scheduled
        }

        constructor(start: Date?, end: Date?, scheduled: Boolean) : this(null, start, end, scheduled)

        var id: Int? = null
        var start: Date? = null
        var end: Date? = null
        var scheduled: Boolean? = null
    }

    var performances: Array<Performance>? = null

    init {
        _images = Images()
    }
}