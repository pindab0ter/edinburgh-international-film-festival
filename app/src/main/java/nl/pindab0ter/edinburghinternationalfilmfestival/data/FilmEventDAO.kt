package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.ContentValues
import android.content.Context
import nl.pindab0ter.edinburghinternationalfilmfestival.data.primitives.FilmEvent
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.databaseStringToDate
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDatabase

class FilmEventDAO(context: Context) {
    private val contentResolver = context.contentResolver

    fun insert(filmEvent: FilmEvent) {
        contentResolver.insert(FilmEventEntry.CONTENT_URI, filmEvent.toContentValues())

        val performanceUrl = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent.code).build()
        filmEvent.performances?.forEach { performance ->
            contentResolver.insert(performanceUrl, performance.toContentValues())
        }
    }

    fun insert(filmEvents: List<FilmEvent>) {
        contentResolver.bulkInsert(FilmEventEntry.CONTENT_URI, filmEvents.map { it.toContentValues() }.toTypedArray())
        filmEvents.forEach { filmEvent ->
            val performanceUrl = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(filmEvent.code).build()
            contentResolver.bulkInsert(performanceUrl, filmEvent.performances?.map { it.toContentValues() }?.toTypedArray())
        }
    }

    fun getAll(): List<FilmEvent> = contentResolver.query(FilmEventEntry.CONTENT_URI, null, null, null, null).run {
        if (count == 0) return emptyList()

        val filmEvents = ArrayList<FilmEvent>()
        moveToFirst()

        do {
            val filmEvent = FilmEvent(
                    getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_DESCRIPTION)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_GENRE_TAGS)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_WEBSITE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED))
            )

            filmEvent.performances = getPerformances(filmEvent.code!!)

            filmEvents.add(filmEvent)
        } while (moveToNext())

        return filmEvents
    }

    private fun getPerformances(code: String): Array<FilmEvent.Performance> = contentResolver.query(PerformanceEntry.CONTENT_URI.buildUpon().appendPath(code).build(), null, null, null, null).run {
        return if (count != 0) {
            val performances = ArrayList<FilmEvent.Performance>()
            moveToFirst()

            do {
                performances.add(FilmEvent.Performance(
                        databaseStringToDate(getString(getColumnIndex(PerformanceEntry.COLUMN_START))),
                        databaseStringToDate(getString(getColumnIndex(PerformanceEntry.COLUMN_END)))
                ))
            } while (moveToNext())

            performances.toTypedArray()
        } else {
            emptyArray()
        }
    }

    fun get(code: String): FilmEvent? {
        val filmEventUrl = FilmEventEntry.CONTENT_URI.buildUpon().appendPath(code).build()
        var filmEvent: FilmEvent? = null

        contentResolver.query(filmEventUrl, null, null, null, null).apply {
            moveToFirst()
            filmEvent = FilmEvent(
                    getString(getColumnIndex(FilmEventEntry.COLUMN_CODE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_TITLE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_DESCRIPTION)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_GENRE_TAGS)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_WEBSITE)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL)),
                    getString(getColumnIndex(FilmEventEntry.COLUMN_UPDATED))
            )
        }

        val performancesUri = PerformanceEntry.CONTENT_URI.buildUpon().appendPath(code).build()
        val performances = ArrayList<FilmEvent.Performance>()
        contentResolver.query(performancesUri, null, null, null, null).apply {
            if (count > 0) {
                moveToFirst()

                do {
                    performances.add(FilmEvent.Performance(
                            databaseStringToDate(getString(getColumnIndex(PerformanceEntry.COLUMN_START))),
                            databaseStringToDate(getString(getColumnIndex(PerformanceEntry.COLUMN_END)))
                    ))
                } while (moveToNext())
            }
            filmEvent?.performances = performances.sortedBy { it.start }.toTypedArray()
        }

        filmEvent?.verifyNotNullFields()

        return filmEvent
    }

    private fun FilmEvent.verifyNotNullFields() {
        require(code != null, { "Film Event code can't be null" })
        require(title != null, { "Film Event title can't be null" })
        require(updated != null, { "Film Event updated date can't be null" })

        performances?.forEach { performance ->
            require(performance.start != null, { "Performance start date can't be null" })
            require(performance.end != null, { "Performance end date can't be null" })
        }
    }

    private fun FilmEvent.toContentValues() = ContentValues().apply {
        put(FilmEventEntry.COLUMN_CODE, this@toContentValues.code)
        put(FilmEventEntry.COLUMN_TITLE, this@toContentValues.title)
        put(FilmEventEntry.COLUMN_DESCRIPTION, this@toContentValues.description)
        put(FilmEventEntry.COLUMN_GENRE_TAGS, this@toContentValues.genreTags?.joinToString())
        put(FilmEventEntry.COLUMN_WEBSITE, this@toContentValues.website?.toString())
        put(FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL, this@toContentValues.imageOriginalUrl?.toString())
        put(FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL, this@toContentValues.imageThumbnailUrl?.toString())
        put(FilmEventEntry.COLUMN_UPDATED, this@toContentValues.updated?.formatForDatabase())
    }

    private fun FilmEvent.Performance.toContentValues() = ContentValues().apply {
        put(PerformanceEntry.COLUMN_START, this@toContentValues.start?.formatForDatabase())
        put(PerformanceEntry.COLUMN_END, this@toContentValues.end?.formatForDatabase())
    }
}