package nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives

import com.google.gson.annotations.SerializedName
import java.util.Date

class FilmEvent {
    val ageCategory: String? = null
    val artist: String? = null
    val artistType: String? = null
    val code: Int? = null
    val country: String? = null
    val description: String? = null
    val descriptionTeaser: String? = null
    val festivalId: String? = null
    val fringeFirst: Boolean? = null
    val genre: String? = null

    @SerializedName("genre_tags")
    private val genreTagsSource: String? = null
    val genreTags: Array<String>?
        get() = genreTagsSource?.split(",")?.toTypedArray()

    val latitude: Double? = null
    val longitude: Double? = null
    val nonEnglish: Boolean? = null
    val performersNumber: Int? = null

    enum class Status {
        @SerializedName("active")
        Active,
        @SerializedName("cancelled")
        Cancelled,
        @SerializedName("deleted")
        Deleted
    }

    val status: Status? = null
    val subTitle: String? = null
    val subVenue: String? = null
    val title: String? = null
    val twitter: String? = null
    val updated: Date? = null
    val url: String? = null
    val warnings: String? = null
    val website: String? = null
    val year: Int? = null

    class Categories {
        val keywords: Array<String>? = null
        val strandTitles: Array<String>? = null
        val subjects: Array<String>? = null
    }

    val categories: Categories? = null

    class Disabled {
        val audio: Boolean? = null
        val audioDates: String? = null
        val captioning: Boolean? = null
        val captioningDates: String? = null
        val otherServices: Boolean? = null
        val otherServicesDates: String? = null
        val otherServicesInformation: Boolean? = null
        val signed: Boolean? = null
        val signedDates: String? = null
    }

    val disabled: Disabled? = null

    class Discounts {
        val friends: Boolean? = null
        val group: Boolean? = null
        val passport: Boolean? = null
        val schools: Boolean? = null
        val twoForOne: Boolean? = null
    }

    val discounts: Discounts? = null

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

    class PerformanceSpace {
        val ageLimit: String? = null
        val ageLimited: Boolean? = null
        val capacity: Int? = null
        val name: String? = null
    }

    val performanceSpace: PerformanceSpace? = null

    class Performance {
        sealed class Concession {
            class Available(val amount: Int) : Concession()
            class Unavailable() : Concession()
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

    class UpdateTimes {
        val ageCategory: Date? = null
        val artist: Date? = null
        val description: Date? = null
        val disabled: Date? = null
        val discounts: Date? = null
        val fringeFirst: Date? = null
        val genre: Date? = null
        val identity: Date? = null
        val images: Date? = null
        val other: Date? = null
        val performances: Date? = null
        val status: Date? = null
        val title: Date? = null
        val venue: Date? = null
        val webLinks: Date? = null
    }

    val updateTimes: UpdateTimes? = null

    class Venue {
        val address: String? = null
        val boxOfficeFringe: String? = null
        val boxOfficeOpening: String? = null
        val cafeDescription: String? = null
        val code: String? = null
        val description: String? = null
        val disabledDescription: String? = null
        val email: String? = null
        val fax: String? = null
        val hasBar: Boolean? = null
        val hasBookingOverCard: Boolean? = null
        val hasBookingOverPhone: Boolean? = null
        val hasBookingOverWeb: Boolean? = null
        val hasCafe: Boolean? = null
        val mapRef: String? = null
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