package nl.pindab0ter.edinburghinternationalfilmfestival.data.network

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.buildUrl
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class EdinburgFestivalCityApiFetcher(private val context: Context, private val listener: (filmEvents: List<FilmEvent>) -> Unit, private val errorListener: (VolleyError) -> Unit) {
    private val logTag = EdinburgFestivalCityApiFetcher::class.simpleName

    fun fetch() {
        val url = buildUrl(context)
        val filmsRequest = FilmEventRequest(url, listener, errorListener)

        thread {
            RequestQueueHolder.getInstance(context).add(filmsRequest)
        }
    }

    fun fetchOffline() {
        val filmEvents: List<FilmEvent> = FilmEventRequest.gson
                .fromJson<Array<FilmEvent>>(EDINBURGH_FILM_FESTIVAL_REPLY, Array<FilmEvent>::class.java)
                .toList()
        listener.invoke(filmEvents)
    }

    class FilmEventRequest(url: URL, private val listener: ((filmEvents: List<FilmEvent>) -> Unit), private val errorListener: (VolleyError) -> Unit
    ) : Request<List<FilmEvent>>(
            Request.Method.GET,
            url.toString(),
            Response.ErrorListener { volleyError ->
                errorListener.invoke(volleyError)
                Log.e(logTag, volleyError.message)
                volleyError.printStackTrace()
            }
    ) {
        companion object {
            private val logTag = FilmEventRequest::class.simpleName

            val gson: Gson = GsonBuilder()
                    .disableHtmlEscaping()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapter(FilmEvent.Images::class.java, ImagesDeserializer())
                    .registerTypeAdapter(Date::class.java, DateDeserializer())
                    .registerTypeAdapter(Uri::class.java, UriDeserializer())
                    .create()
        }

        override fun getHeaders(): MutableMap<String, String>? = mutableMapOf("Accept" to "application/json;ver=2.0")

        override fun parseNetworkResponse(response: NetworkResponse): Response<List<FilmEvent>> {
            val parsed: String = try {
                String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
            } catch (e: UnsupportedEncodingException) {
                String(response.data)
            }

            val filmEvents = gson.fromJson<Array<FilmEvent>>(parsed, Array<FilmEvent>::class.java)

            return Response.success(filmEvents.asList(), HttpHeaderParser.parseCacheHeaders(response))
        }

        override fun deliverResponse(response: List<FilmEvent>) = listener.invoke(response.sortedBy { it.title })

        class ImagesDeserializer : JsonDeserializer<FilmEvent.Images> {
            override fun deserialize(element: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FilmEvent.Images {
                val images = with(element.asJsonObject) {
                    if (has("hash")) this
                    else entrySet().first().value
                }

                return context.deserialize<FilmEvent.Images>(images, FilmEvent.ImagesSubclass::class.java)
            }
        }

        class DateDeserializer : JsonDeserializer<Date> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date = try {
                SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.getDefault()).parse(json.asString)
            } catch (exception: Exception) {
                Date(json.asLong * 1000)
            } catch (exception: Exception) {
                throw IllegalArgumentException("Could not parse $json as a Date")
            }
        }

        class UriDeserializer : JsonDeserializer<Uri> {
            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Uri? {
                json?.asString?.trim('\"')?.let { urlString ->
                    return if (urlString.startsWith("//", false)) Uri.parse("https:$urlString")
                    else Uri.parse(urlString)
                }
                return null
            }
        }
    }
}
