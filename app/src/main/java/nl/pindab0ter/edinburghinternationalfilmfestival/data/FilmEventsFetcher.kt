package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.Context
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.EdinburghFestivalCityUtilities
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.RequestQueueHolder

class FilmEventsFetcher(private val context: Context, private val listener: (filmEvents: List<FilmEvent>) -> Unit) {
    private val logTag = FilmEventsFetcher::class.simpleName

    fun fetch() {
        val url = EdinburghFestivalCityUtilities.buildUrl(context)

        Log.v(logTag, "Sending request to $url")

        val filmsRequest = FilmEventsRequest(url, listener) { volleyError ->
            Log.e(logTag, "$volleyError")
        }

        RequestQueueHolder.getInstance(context).add(filmsRequest)
    }
}