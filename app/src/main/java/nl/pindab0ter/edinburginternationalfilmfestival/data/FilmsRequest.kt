package nl.pindab0ter.edinburginternationalfilmfestival.data

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException
import java.net.URL
import java.nio.charset.Charset


class FilmsRequest(url: URL, listener: ((error: VolleyError) -> Unit)) : Request<String>(Request.Method.GET, url.toString(), listener) {

    private val logTag = FilmsRequest::class.simpleName

    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        val parsed: String = try {
            String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
        } catch (e: UnsupportedEncodingException) {
            String(response.data)
        }

        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: String?) {
        Log.v(logTag, response)
    }

    override fun getHeaders(): MutableMap<String, String>? = mutableMapOf("Accept" to "application/json;ver=2.0")
}