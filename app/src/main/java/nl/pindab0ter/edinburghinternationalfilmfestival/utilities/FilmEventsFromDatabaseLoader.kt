package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEventDAO
import nl.pindab0ter.edinburghinternationalfilmfestival.data.model.FilmEvent

class FilmEventsFromDatabaseLoader(context: Context) : AsyncTaskLoader<List<FilmEvent>>(context) {
    init {
        onContentChanged()
    }

    override fun loadInBackground(): List<FilmEvent>? {
        return FilmEventDAO(context).getAll()
    }

    override fun onStartLoading() {
        if (takeContentChanged()) forceLoad()
        super.onStartLoading()
    }
}