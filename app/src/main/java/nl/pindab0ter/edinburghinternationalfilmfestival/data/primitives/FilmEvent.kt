package nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives

import com.google.gson.annotations.SerializedName
import java.util.Date

class FilmEvent {
    val code: String? = null
    val description: String? = null
    val genre: String? = null

    @SerializedName("genre_tags")
    private val genreTagsSource: String? = null
    val genreTags: Array<String>?
        get() = genreTagsSource?.split(",")?.map { it.trim() }?.toTypedArray()

    val latitude: Double? = null
    val longitude: Double? = null

    enum class Status {
        @SerializedName("active")
        Active,
        @SerializedName("cancelled")
        Cancelled,
        @SerializedName("deleted")
        Deleted
    }

    val status: Status? = null
    val title: String? = null
    val updated: Date? = null
    val url: String? = null
    val website: String? = null
    val year: Int? = null

    open class Images {

        val hash: String? = null

        enum class Orientation {
            @SerializedName("landscape")
            Landscape,
            @SerializedName("portrait")
            Portrait
        }

        val orientation: Orientation? = null
        val type: String? = null

        class Versions {
            class Version {
                val type: String? = null
                val mime: String? = null
                val height: Int? = null
                val width: Int? = null
                val url: String? = null
            }

            @SerializedName("original")
            val original: Version? = null
            @SerializedName("square-75")
            val square75: Version? = null
            @SerializedName("square-150")
            val square150: Version? = null
            @SerializedName("thumb-100")
            val thumb100: Version? = null
            @SerializedName("medium-640")
            val medium640: Version? = null
            @SerializedName("small-320")
            val small320: Version? = null
            @SerializedName("large-1024")
            val large1024: Version? = null
        }

        val versions: Versions? = null
    }

    // Subclass to prevent recursive deserialization
    class ImagesSubclass : Images()

    val images: Images? = null

    class Performance {
        sealed class Concession {
            class Available(val amount: Int) : Concession()
            class Unavailable : Concession()
        }

        val concession: Concession? = null
        val concessionAdditional: Concession? = null
        val concessionFamily: Concession? = null
        val start: Date? = null
        val end: Date? = null
        val price: Int? = null
        val title: String? = null
    }

    val performances: Array<Performance>? = null

    class Venue {
        val address: String? = null
        val code: String? = null
        val description: String? = null
        val email: String? = null
        val name: String? = null
        val phone: String? = null

        class Position {
            val lat: Double? = null
            val lon: Double? = null
        }

        val position: Position? = null
        val postCode: String? = null
        val webAddress: String? = null
    }

    val venue: Venue? = null
}