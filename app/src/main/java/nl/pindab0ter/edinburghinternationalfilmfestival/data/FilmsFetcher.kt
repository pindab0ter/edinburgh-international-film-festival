package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.Context
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.EdinburghFestivalCityUtilities
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.RequestQueueHolder

class FilmsFetcher(private val context: Context) {
    private val logTag = FilmsFetcher::class.simpleName

    fun fetch() {
        val url = EdinburghFestivalCityUtilities.buildUrl(context)

        val filmsRequest = FilmsRequest(url) { volleyError ->
            Log.e(logTag, "$volleyError")
        }

        RequestQueueHolder.getInstance(context).add(filmsRequest)
    }
}