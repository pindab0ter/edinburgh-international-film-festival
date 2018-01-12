package nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName

import java.lang.reflect.Type
import java.util.ArrayList

class Film {
    var title: String? = null
    var images: Image? = null /* You must register the custom ImagesDeserializer (below) to
                                parse this attribute correctly */


    /**
     * Returns the url of the first image that has type thumb
     */
//    val thumbUrl: String?
//        get() = images
//                ?.firstOrNull { it.type == "thumb" }
//                ?.let { it.versions.square?.url }

    /** This class doesn't correspond to any key in the JSON of the festival API.
     * Register the ImagesDeserializer as a typeAdapter at the GSONBuilder
     *
     * Gson gson = new GsonBuilder()
     * .registerTypeAdapter(Film.Image[].class,
     * new Film.ImagesDeserializer())
     * .create();
     *
     * now call  gson.fromJson(the response object with the data, the class you need) to
     * obtain the data
     */
    class Image(
            var type: String,
            var versions: Versions
    )

    class Versions(
            @SerializedName("large-1024")
            var large: ImageVersion? = null,
            @SerializedName("square-75")
            var square: ImageVersion? = null
    )

    class ImageVersion {
        var url: String? = null
    }

    /**
     * The festival api returns the images as a object with hash-keys with each hash-key holding
     * all data (and versions) of a particular image.
     * Because each hash-key is unique we need some custom deserialization to get all images
     * in an array.
     */
    class ImagesDeserializer : JsonDeserializer<Array<Image>> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Array<Film.Image> {

            val festivalImagesList = ArrayList<Image>()

            /* Iterate over all images (the hashes) in the JSON data and put the versions data
            in a new array list */
            val it = json.asJsonObject.entrySet().iterator()
            while (it.hasNext()) {

                /* Grab one image node */
                val imageJson = it.next().value.asJsonObject

                /* Get the type node */
                val jsonTypeData = imageJson.get("type")
                /* Get the data in the type node. Luckily this data is just a string */
                val imageType = context.deserialize<String>(jsonTypeData, String::class.java)

                /* Get all versions node*/
                val jsonVersionsData = imageJson.get("versions")
                /* The data in the versions node is another JSON object with element so we need to traverse
                * these nodes as well. Luckily we can just use the default GSON deserializer providing
                * our Versions class defined above */
                val imageVersions = context.deserialize<Film.Versions>(jsonVersionsData, Film.Versions::class.java)

                /* Create a new Festival.Image node object and add it to the list of images*/
                val filmImage = Film.Image(imageType, imageVersions)
                festivalImagesList.add(filmImage)
            }

            return festivalImagesList.toTypedArray()
        }

        /**
         * The festival api returns the images as a object with hash-keys with each hash-key holding
         * all data (and versions) of a particular image.
         * Because each hash-key is unique we need some custom deserialization to get all images
         * in an array.
         */
        class ImagesDeserializer : JsonDeserializer<Array<Image>> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Array<Film.Image> {
                val festivalImagesList = ArrayList<Image>()

                /* Iterate over all images (the hashes) in the JSON data and put the versions data in a new array list */
                json.asJsonObject.entrySet().mapTo(festivalImagesList) {
                    val imageJson = it.value.asJsonObject

                    /* Get the data in the type node */
                    val imageType = context.deserialize<String>(imageJson.get("type"), String::class.java)

                    /* Get all version nodes */
                    val jsonVersionsData = imageJson.get("versions")

                    /* The data in the versions node is another JSON object with element so we need to traverse
                    * these nodes as well. Luckily we can just use the default GSON deserializer providing
                    * our Versions class defined above */
                    val imageVersions = context.deserialize<Film.Versions>(jsonVersionsData, Film.Versions::class.java)

                    /* Create a new Festival.Image node object and add it to the list of images*/
                    return@mapTo Film.Image(imageType, imageVersions)
                }

                return festivalImagesList.toTypedArray()
            }
        }
    }
}