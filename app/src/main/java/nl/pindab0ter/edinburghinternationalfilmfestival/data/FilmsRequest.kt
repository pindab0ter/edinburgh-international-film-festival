package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.Film
import java.io.UnsupportedEncodingException
import java.net.URL
import java.nio.charset.Charset


class FilmsRequest(url: URL, listener: ((error: VolleyError) -> Unit)) : Request<List<Film>>(Request.Method.GET, url.toString(), listener) {
    private val logTag = FilmsRequest::class.simpleName
    private val gson = Gson()

    override fun parseNetworkResponse(response: NetworkResponse): Response<List<Film>> {
        val parsed: String = try {
            String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
        } catch (e: UnsupportedEncodingException) {
            String(response.data)
        }

        val films = gson.fromJson<Array<Film>>(parsed, Array<Film>::class.java)

        return Response.success(films.asList(), HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: List<Film>?) {
        Log.v(logTag, response?.joinToString())
    }

    override fun getHeaders(): MutableMap<String, String>? = mutableMapOf("Accept" to "application/json;ver=2.0")
}