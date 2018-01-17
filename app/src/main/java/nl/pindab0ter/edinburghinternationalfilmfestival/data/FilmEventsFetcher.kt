package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.Context
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.dummy.EDINBURGH_FILM_FESTIVAL_REPLY
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.EdinburghFestivalCityUtilities
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.RequestQueueHolder

class FilmEventsFetcher(private val context: Context, private val listener: (filmEvents: List<FilmEvent>) -> Unit) {
    private val logTag = FilmEventsFetcher::class.simpleName

    fun fetch() {
        val url = EdinburghFestivalCityUtilities.buildUrl(context)

        Log.v(logTag, "Requesting Film Events with url: $url")

        val filmsRequest = FilmEventsRequest(url, listener) { volleyError ->
            Log.e(logTag, "$volleyError")
        }

        RequestQueueHolder.getInstance(context).add(filmsRequest)
    }

    fun fetchOffline() {
        val filmEvents: List<FilmEvent> = FilmEventsRequest.gson
                .fromJson<Array<FilmEvent>>(EDINBURGH_FILM_FESTIVAL_REPLY, Array<FilmEvent>::class.java)
                .toList()
        listener.invoke(filmEvents)
    }
}