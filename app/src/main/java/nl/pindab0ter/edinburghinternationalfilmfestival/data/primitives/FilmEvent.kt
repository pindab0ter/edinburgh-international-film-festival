package nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives

import android.media.Image
import com.google.gson.annotations.SerializedName
import java.net.URL
import java.util.Date

class FilmEvent {
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
        get() = URL(images?.versions?.original?.url)
        set(value) {
            images?.versions?.original?.url = value.toString()
        }

    var imageThumbnail: Image? = null
    var imageThumbnailUrl: URL?
        get() = URL(images?.versions?.thumb100?.url)
        set(value) {
            images?.versions?.thumb100?.url = value.toString()
        }

    open class Images {
        class Versions {
            class Version {
                var url: String? = null
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

    class Performance {
        var start: Date? = null
        var end: Date? = null
        var price: Int? = null
    }

    var performances: Array<Performance>? = null
}