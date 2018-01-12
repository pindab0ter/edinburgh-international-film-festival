package nl.pindab0ter.edinburghinternationalfilmfestival.data

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import java.io.UnsupportedEncodingException
import java.net.URL
import java.nio.charset.Charset
import java.util.*


// TODO: Differentiate between listener and errorListener
class FilmEventsRequest(url: URL, listener: ((error: VolleyError) -> Unit)) : Request<List<FilmEvent>>(Request.Method.GET, url.toString(), listener) {
    private val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(FilmEvent.Images::class.java, FilmEvent.ImagesDeserializer())
            .registerTypeAdapter(Array<FilmEvent.Images.Version>::class.java, FilmEvent.VersionsDeserializer())
            .registerTypeAdapter(FilmEvent.Performance.Concession::class.java, FilmEvent.ConcessionDeserializer())
            .registerTypeAdapter(Date::class.java, FilmEvent.DateDeserializer())
            .create()

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

    override fun deliverResponse(response: List<FilmEvent>?) {
        TODO("not implemented")
    }
}