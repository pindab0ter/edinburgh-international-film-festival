package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.android.volley.VolleyError
import nl.pindab0ter.edinburghinternationalfilmfestival.data.network.FilmEventFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.GetFilmEventsFromDatabaseTask
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.InsertFilmEventsIntoDatabaseTask

class FilmEventViewModel(application: Application) : AndroidViewModel(application) {
    val filmEvents = MutableLiveData<List<FilmEvent>>()
        get() {
            if (field.value == null) GetFilmEventsFromDatabaseTask(getApplication(), onFilmEventsFromDb).execute()
            return field
        }

    private val onFilmEventsFromDb: (filmEvents: List<FilmEvent>) -> Unit = { filmEventsFromDb ->
        if (filmEventsFromDb.isNotEmpty()) this.filmEvents.value = filmEventsFromDb
        else FilmEventFetcher(getApplication(), onFilmEventsFromApi, onFilmEventsFromApiFail).fetch()
    }

    private val onFilmEventsFromApi: (filmEvents: List<FilmEvent>) -> Unit = { filmEventsFromApi ->
        if (filmEventsFromApi.isNotEmpty()) {
            this.filmEvents.value = filmEventsFromApi
            InsertFilmEventsIntoDatabaseTask(getApplication()).execute(filmEventsFromApi)
        } else filmEvents.value = null
    }

    private val onFilmEventsFromApiFail: (error: VolleyError) -> Unit = {
        filmEvents.value = null
    }
}
