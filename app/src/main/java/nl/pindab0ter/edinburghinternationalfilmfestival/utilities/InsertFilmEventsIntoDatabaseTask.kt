package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.annotation.SuppressLint
import android.os.AsyncTask
import nl.pindab0ter.edinburghinternationalfilmfestival.MainActivity
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

@SuppressLint("StaticFieldLeak")
class InsertFilmEventsIntoDatabaseTask(private val mainActivity: MainActivity) : AsyncTask<List<FilmEvent>, Unit, Unit>() {
    override fun doInBackground(vararg params: List<FilmEvent>?) {
        FilmEventDAO(mainActivity).insert(params.first().orEmpty())
    }
}