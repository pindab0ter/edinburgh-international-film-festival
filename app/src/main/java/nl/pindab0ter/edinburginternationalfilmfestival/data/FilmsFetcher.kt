package nl.pindab0ter.edinburginternationalfilmfestival.data

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.Volley
import nl.pindab0ter.edinburginternationalfilmfestival.utilities.EdinburghFestivalCityUtilities

class FilmsFetcher(private val context: Context) {
    private val requestQueue = Volley.newRequestQueue(context)

    private val logTag = FilmsFetcher::class.simpleName

    fun fetch() {
        val url = EdinburghFestivalCityUtilities.buildUrl(context)

        val stringRequest = FilmsRequest(url) { volleyError ->
            Log.e(logTag, "$volleyError")
        }

        requestQueue.add(stringRequest)
    }
}