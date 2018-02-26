package nl.pindab0ter.edinburghinternationalfilmfestival.data.network

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.*
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.dummy.EDINBURGH_FILM_FESTIVAL_REPLY
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.RequestQueueHolder
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.buildUrl
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class FilmEventFetcher(private val context: Context, private val listener: (filmEvents: List<FilmEvent>) -> Unit, private val errorListener: (volleyError: VolleyError) -> Unit) {
    private val logTag = FilmEventFetcher::class.simpleName

    fun fetch() {
        val url = buildUrl(context)
        val filmsRequest = FilmEventRequest(url, listener, errorListener)

        RequestQueueHolder.getInstance(context).add(filmsRequest)
    }

    fun fetchOffline() {
        val filmEvents: List<FilmEvent> = FilmEventRequest.gson
                .fromJson<Array<FilmEvent>>(EDINBURGH_FILM_FESTIVAL_REPLY, Array<FilmEvent>::class.java)
                .toList()
        listener.invoke(filmEvents)
    }

    class FilmEventRequest(
            url: URL,
            private val listener: ((filmEvents: List<FilmEvent>) -> Unit),
            errorListener: ((error: VolleyError) -> Unit)
    ) : Request<List<FilmEvent>>(Request.Method.GET, url.toString(), errorListener) {
        companion object {
            val gson: Gson = GsonBuilder()
                    .disableHtmlEscaping()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapter(FilmEvent.Images::class.java, ImagesDeserializer())
                    .registerTypeAdapter(FilmEvent.Performance.Concession::class.java, ConcessionDeserializer())
                    .registerTypeAdapter(Date::class.java, DateDeserializer())
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

        override fun deliverResponse(response: List<FilmEvent>) = listener.invoke(response)

        class ImagesDeserializer : JsonDeserializer<FilmEvent.Images> {
            override fun deserialize(element: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FilmEvent.Images {
                val images = with(element.asJsonObject) {
                    if (has("hash")) this
                    else entrySet().first().value
                }

                return context.deserialize<FilmEvent.Images>(images, FilmEvent.ImagesSubclass::class.java)
            }
        }

        class ConcessionDeserializer : JsonDeserializer<FilmEvent.Performance.Concession> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FilmEvent.Performance.Concession = try {
                FilmEvent.Performance.Concession.Available(json.asInt)
            } catch (exception: NumberFormatException) {
                FilmEvent.Performance.Concession.Unavailable()
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
    }
}