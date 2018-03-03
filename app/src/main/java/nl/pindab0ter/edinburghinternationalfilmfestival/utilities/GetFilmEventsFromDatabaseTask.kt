package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.annotation.SuppressLint
import android.os.AsyncTask
import nl.pindab0ter.edinburghinternationalfilmfestival.MainActivity
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

@SuppressLint("StaticFieldLeak")
class GetFilmEventsFromDatabaseTask(private val mainActivity: MainActivity, private val listener: (List<FilmEvent>) -> Unit) : AsyncTask<Unit, Unit, List<FilmEvent>>() {
    override fun doInBackground(vararg params: Unit?): List<FilmEvent>? {
        return FilmEventDAO(mainActivity).getAll()
    }

    override fun onPostExecute(filmEvents: List<FilmEvent>) = listener(filmEvents)
}