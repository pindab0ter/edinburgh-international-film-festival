package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.ListActivity
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.network.FilmEventFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

@SuppressLint("StaticFieldLeak")
class GetFilmEventsFromDatabaseTask(private val listActivity: ListActivity, private val listener: (List<FilmEvent>) -> Unit) : AsyncTask<Unit, Unit, List<FilmEvent>>() {
    override fun doInBackground(vararg params: Unit?): List<FilmEvent>? {
        return FilmEventDAO(listActivity).getAll()
    }

    override fun onPostExecute(filmEvents: List<FilmEvent>) = listener(filmEvents)
}