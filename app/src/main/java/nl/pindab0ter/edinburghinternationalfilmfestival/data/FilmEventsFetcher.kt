package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.Context
import com.android.volley.VolleyError
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.dummy.EDINBURGH_FILM_FESTIVAL_REPLY
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.RequestQueueHolder
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.buildUrl

class FilmEventsFetcher(private val context: Context, private val listener: (filmEvents: List<FilmEvent>) -> Unit, private val errorListener: (volleyError: VolleyError) -> Unit) {
    private val logTag = FilmEventsFetcher::class.simpleName

    fun fetch() {
        val url = buildUrl(context)

        val filmsRequest = FilmEventsRequest(url, listener, errorListener)

        RequestQueueHolder.getInstance(context).add(filmsRequest)
    }

    fun fetchOffline() {
        val filmEvents: List<FilmEvent> = FilmEventsRequest.gson
                .fromJson<Array<FilmEvent>>(EDINBURGH_FILM_FESTIVAL_REPLY, Array<FilmEvent>::class.java)
                .toList()
        listener.invoke(filmEvents)
    }
}