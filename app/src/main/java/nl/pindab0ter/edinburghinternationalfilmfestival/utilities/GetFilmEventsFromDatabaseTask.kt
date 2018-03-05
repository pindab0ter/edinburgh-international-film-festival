package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.content.Context
import android.os.AsyncTask
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import java.lang.ref.WeakReference

class GetFilmEventsFromDatabaseTask(context: Context, private val listener: (List<FilmEvent>) -> Unit) : AsyncTask<Unit, Unit, List<FilmEvent>>() {
    private val context: WeakReference<Context> = WeakReference(context)

    override fun doInBackground(vararg params: Unit?): List<FilmEvent>? {
        return context.get()?.let { FilmEventDAO(it).getAll() } ?: emptyList()
    }

    override fun onPostExecute(filmEvents: List<FilmEvent>) = listener(filmEvents)
}