package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.ContentValues
import android.content.Context
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.databaseStringToDate
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDatabase

class FilmEventDAO(context: Context) {
    private val contentResolver = context.contentResolver

    fun insert(filmEvent: FilmEvent) {
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent.toContentValues())

        val performanceUrl = PerformanceEntry.BY_FILM_EVENT_CODE_URI.buildUpon().appendPath(filmEvent.code).build()
        filmEvent.performances?.forEach { performance ->
            contentResolver.insert(performanceUrl, performance.toContentValues())
        }
    }

    fun insert(filmEvents: List<FilmEvent>) {
        contentResolver.bulkInsert(FilmEventEntry.CONTENT_URI, filmEvents.map { it.toContentValues() }.toTypedArray())
        filmEvents.forEach { filmEvent ->
            val performanceUrl = PerformanceEntry.BY_FILM_EVENT_CODE_URI.buildUpon().appendPath(filmEvent.code).build()
            contentResolver.bulkInsert(performanceUrl, filmEvent.performances?.map { it.toContentValues() }?.toTypedArray())
        }
    }

    fun getAll(): List<FilmEvent> = contentResolver.query(FilmEventEntry.CONTENT_URI, null, null, null, null).run {
        if (count == 0) {
            close()
            return emptyList()
        }

        val filmEvents = ArrayList<FilmEvent>()
        moveToFirst()

        do {
            val filmEvent = FilmEvent(
                    getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_DESCRIPTION)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_GENRE_TAGS)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_WEBSITE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED))
            )

            filmEvent.performances = getPerformances(filmEvent.code!!)

            filmEvents.add(filmEvent)
        } while (moveToNext())

        close()
        return filmEvents
    }

    private fun getPerformances(code: String): Array<FilmEvent.Performance> = contentResolver.query(PerformanceEntry.BY_FILM_EVENT_CODE_URI.buildUpon().appendPath(code).build(), null, null, null, null).run {
        if (count == 0) {
            close()
            return emptyArray()
        }

        val performances = ArrayList<FilmEvent.Performance>()
        moveToFirst()

        do {
            performances.add(FilmEvent.Performance(
                    getInt(getColumnIndex(PerformanceEntry.COLUMN_ID)),
                    databaseStringToDate(getString(getColumnIndex(PerformanceEntry.COLUMN_START))),
                    databaseStringToDate(getString(getColumnIndex(PerformanceEntry.COLUMN_END))),
                    getInt(getColumnIndex(PerformanceEntry.COLUMN_SCHEDULED)).asBoolean()
            ))
        } while (moveToNext())

        close()
        performances.toTypedArray()
    }

    fun get(code: String): FilmEvent? {
        val filmEventUrl = FilmEventEntry.BY_CODE_URI.buildUpon().appendPath(code).build()
        var filmEvent: FilmEvent? = null

        contentResolver.query(filmEventUrl, null, null, null, null).apply {
            if (count == 0) {
                close()
                return null
            }

            moveToFirst()
            filmEvent = FilmEvent(
                    getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_DESCRIPTION)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_GENRE_TAGS)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_WEBSITE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED))
            )

            close()
        }

        filmEvent?.performances = getPerformances(code).sortedBy { it.start }.toTypedArray()
        filmEvent?.verifyNotNullFields()

        return filmEvent
    }

    fun update(performance: FilmEvent.Performance) {
        val performanceUri = PerformanceEntry.BY_ID_URI.buildUpon().appendPath("${performance.id}").build()
        contentResolver.update(performanceUri, performance.toContentValues(), null, null)
    }

    private fun Boolean.asInt() = if (this) 1 else 0
    private fun Int.asBoolean() = this == 1

    private fun FilmEvent.verifyNotNullFields() {
        require(code != null, { "Film Event code can't be null" })
        require(title != null, { "Film Event title can't be null" })
        require(updated != null, { "Film Event updated date can't be null" })

        performances?.forEach { performance ->
            require(performance.id != null, { "Performance id can't be null" })
            require(performance.start != null, { "Performance start date can't be null" })
            require(performance.end != null, { "Performance end date can't be null" })
            require(performance.scheduled != null, { "Performance scheduled state can't be null" })
        }
    }

    private fun FilmEvent.toContentValues() = ContentValues().apply {
        put(FilmEventEntry.COLUMN_CODE, this@toContentValues.code)
        put(FilmEventEntry.COLUMN_TITLE, this@toContentValues.title)
        put(FilmEventEntry.COLUMN_DESCRIPTION, this@toContentValues.description)
        put(FilmEventEntry.COLUMN_GENRE_TAGS, this@toContentValues.genreTags?.joinToString())
        put(FilmEventEntry.COLUMN_WEBSITE, this@toContentValues.website?.toString())
        put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL, this@toContentValues.imageOriginal?.toString())
        put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL, this@toContentValues.imageThumbnail?.toString())
        put(FilmEventEntry.COLUMN_UPDATED, this@toContentValues.updated?.formatForDatabase())
    }

    private fun FilmEvent.Performance.toContentValues() = ContentValues().apply {
        put(PerformanceEntry.COLUMN_ID, this@toContentValues.id)
        put(PerformanceEntry.COLUMN_START, this@toContentValues.start?.formatForDatabase())
        put(PerformanceEntry.COLUMN_END, this@toContentValues.end?.formatForDatabase())
        put(PerformanceEntry.COLUMN_SCHEDULED, this@toContentValues.scheduled?.asInt())
    }
}