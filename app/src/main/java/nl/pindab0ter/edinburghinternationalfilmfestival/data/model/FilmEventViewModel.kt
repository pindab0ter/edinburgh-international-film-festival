package nl.pindab0ter.edinburghinternationalfilmfestival.data.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.v4.content.Loader
import com.android.volley.VolleyError
import nl.pindab0ter.edinburghinternationalfilmfestival.data.network.EdinburghFestivalCityApiFetcher
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.FilmEventsFromDatabaseLoader
import kotlin.concurrent.thread

class FilmEventViewModel(application: Application) : AndroidViewModel(application), Loader.OnLoadCompleteListener<List<FilmEvent>> {
    private val getFilmEventsFromDatabaseTask = FilmEventsFromDatabaseLoader(getApplication()).apply {
        this.registerListener(id, this@FilmEventViewModel)
    }

    val filmEvents = MutableLiveData<List<FilmEvent>>()
        get() {
            if (field.value == null) getFilmEventsFromDatabaseTask.startLoading()
            return field
        }

    fun refresh() {
        getFilmEventsFromDatabaseTask.forceLoad()
    }

    override fun onLoadComplete(loader: Loader<List<FilmEvent>>, filmEventsFromDb: List<FilmEvent>?) {
        if (filmEventsFromDb != null && filmEventsFromDb.isNotEmpty()) this.filmEvents.value = filmEventsFromDb
        else EdinburghFestivalCityApiFetcher(getApplication(), onFilmEventsFromApi, onFilmEventsFromApiFail).fetch()
    }

    private val onFilmEventsFromApi: (filmEvents: List<FilmEvent>) -> Unit = { filmEventsFromApi ->
        if (filmEventsFromApi.isNotEmpty()) {
            this.filmEvents.value = filmEventsFromApi
            thread {
                FilmEventDAO(getApplication()).insert(filmEventsFromApi)
            }
        } else filmEvents.value = null
    }

    private val onFilmEventsFromApiFail: (error: VolleyError) -> Unit = {
        filmEvents.value = null
    }
}
