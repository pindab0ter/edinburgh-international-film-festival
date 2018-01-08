package nl.pindab0ter.edinburginternationalfilmfestival.data

import android.content.Context
import android.util.Log
import com.android.volley.Request.Method.GET
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class FilmsFetcher(context: Context) {
    private val logTag = FilmsFetcher::class.java.simpleName!!
    private val requestQueue = Volley.newRequestQueue(context)

    fun fetch() {
        val url = "https://api.edinburghfestivalcity.com//events?festival=film&year=2017&size=100&key=6ZC5r7r4TaNKScAF&signature=f8f7f86711385778a559f0eeb003ab528e7c8be0"

        val stringRequest = StringRequest(GET, url, { response ->
            Log.v(logTag, response)
        }, { error ->
            Log.e(logTag, error.toString())
        })

        requestQueue.add(stringRequest)
    }
}