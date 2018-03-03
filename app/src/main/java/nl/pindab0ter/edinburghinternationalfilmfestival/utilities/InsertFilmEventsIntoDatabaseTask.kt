package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.annotation.SuppressLint
import android.os.AsyncTask
import nl.pindab0ter.edinburghinternationalfilmfestival.ListActivity
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent

@SuppressLint("StaticFieldLeak")
class InsertFilmEventsIntoDatabaseTask(private val listActivity: ListActivity) : AsyncTask<List<FilmEvent>, Unit, Unit>() {
    override fun doInBackground(vararg params: List<FilmEvent>?) {
        FilmEventDAO(listActivity).insert(params.first().orEmpty())
    }
}