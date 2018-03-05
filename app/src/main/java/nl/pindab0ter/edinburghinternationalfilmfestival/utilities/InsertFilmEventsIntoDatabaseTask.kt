package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.content.Context
import android.os.AsyncTask
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import java.lang.ref.WeakReference

class InsertFilmEventsIntoDatabaseTask(context: Context) : AsyncTask<List<FilmEvent>, Unit, Unit>() {
    private val context: WeakReference<Context> = WeakReference(context)

    override fun doInBackground(vararg params: List<FilmEvent>?) {
        context.get()?.let { FilmEventDAO(it).insert(params.first().orEmpty()) }
    }
}