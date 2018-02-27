package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.CONTENT_AUTHORITY
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PATH_FILM_EVENTS
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PATH_FILM_EVENT_BY_ID
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PATH_PERFORMANCES
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventContract.PATH_PERFORMANCE_BY_FILM_EVENT_CODE

class FilmEventProvider : ContentProvider() {
    private lateinit var filmEventDbHelper: FilmEventDbHelper
    private val logTag = FilmEventProvider::class.simpleName

    override fun onCreate(): Boolean {
        filmEventDbHelper = FilmEventDbHelper(context)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri = with(filmEventDbHelper.writableDatabase) {
        when (uriMatcher.match(uri)) {
            CODE_FILM_EVENTS -> {
                beginTransaction()
                val id = insert(FilmEventEntry.TABLE_NAME, null, values)
                val code = values?.getAsString(FilmEventEntry.COLUMN_CODE)
                return if (id != -1L) {
                    setTransactionSuccessful()
                    endTransaction()
                    Log.v(logTag, "Inserted film_event with code $code")
                    FilmEventEntry.CONTENT_URI.buildUpon()
                            .appendPath(code)
                            .build()
                } else {
                    endTransaction()
                    // TODO: Propagate SQLError?
                    Uri.EMPTY
                }
            }
            CODE_PERFORMANCES_BY_FILM_EVENT_CODE -> {
                beginTransaction()
                val filmEventCode = uri.lastPathSegment
                values?.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEventCode)
                val id = insert(PerformanceEntry.TABLE_NAME, null, values)
                return if (id != -1L) {
                    setTransactionSuccessful()
                    endTransaction()
                    Log.v(logTag, "Inserted performance for film_event with code $filmEventCode")
                    PerformanceEntry.CONTENT_URI.buildUpon()
                            .appendPath(id.toString())
                            .build()
                } else {
                    endTransaction()
                    Uri.EMPTY
                }
            }
            else -> throw IllegalArgumentException("No match found for uri $uri")
        }
    }

    override fun bulkInsert(uri: Uri, valuesArray: Array<out ContentValues>?): Int = with(filmEventDbHelper.writableDatabase) {
        when (uriMatcher.match(uri)) {
            CODE_FILM_EVENTS -> {
                beginTransaction()

                var rowsInserted = 0
                valuesArray?.forEach { values ->
                    val id = insert(FilmEventEntry.TABLE_NAME, null, values)
                    if (id != -1L) rowsInserted++
                }

                if (rowsInserted > 0) setTransactionSuccessful()
                Log.v(logTag, "Inserted $rowsInserted into ${FilmEventEntry.TABLE_NAME}")
                endTransaction()
                rowsInserted
            }
            CODE_PERFORMANCES_BY_FILM_EVENT_CODE -> {
                beginTransaction()

                val filmEventCode = uri.lastPathSegment

                var rowsInserted = 0
                valuesArray?.forEach { values ->
                    values.put(PerformanceEntry.COLUMN_FILM_EVENT_CODE, filmEventCode)
                    val id = insert(PerformanceEntry.TABLE_NAME, null, values)
                    if (id != -1L) rowsInserted++
                }

                if (rowsInserted > 0) setTransactionSuccessful()
                Log.v(logTag, "Inserted $rowsInserted into ${PerformanceEntry.TABLE_NAME}")
                endTransaction()
                rowsInserted
            }
            else -> throw IllegalArgumentException("No match found for uri $uri")
        }
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor = with(filmEventDbHelper.readableDatabase) {
        fun performTransaction(table: String, selection: String?, selectionArgs: Array<out String>?, logMessage: String): Cursor {
            Log.v(logTag, logMessage)
            beginTransaction()
            val cursor = query(table, null, selection, selectionArgs, null, null, null)
            endTransaction()

            return cursor
        }

        fun performTransaction(table: String, logMessage: String): Cursor = performTransaction(table, null, null, logMessage)

        when (uriMatcher.match(uri)) {
            CODE_FILM_EVENTS -> performTransaction(FilmEventEntry.TABLE_NAME, "Query for all film events")
            CODE_FILM_EVENT_BY_CODE -> performTransaction(
                    FilmEventEntry.TABLE_NAME,
                    "${FilmEventEntry.COLUMN_CODE} = ?",
                    Array(1) { uri.lastPathSegment },
                    "Query for film event with id: ${uri.lastPathSegment}"
            )
            CODE_PERFORMANCES -> performTransaction(PerformanceEntry.TABLE_NAME, "Query for all film performances")
            CODE_PERFORMANCES_BY_FILM_EVENT_CODE -> performTransaction(
                    PerformanceEntry.TABLE_NAME,
                    "${PerformanceEntry.COLUMN_FILM_EVENT_CODE} = ?",
                    Array(1) { uri.lastPathSegment },
                    "Query for performances with film_event_code: ${uri.lastPathSegment}"
            )
            else -> throw IllegalArgumentException("No match found for uri $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = with(filmEventDbHelper.writableDatabase) {
        fun performTransaction(table: String, selection: String?, selectionArgs: Array<out String>?): Int {
            beginTransaction()
            val deleted = delete(table, selection, selectionArgs)
            setTransactionSuccessful()
            endTransaction()
            Log.v(logTag, "Deleted $deleted rows")
            return deleted
        }

        fun performTransaction(table: String): Int = performTransaction(table, null, null)

        when (uriMatcher.match(uri)) {
            CODE_FILM_EVENTS -> performTransaction(FilmEventEntry.TABLE_NAME)
            CODE_FILM_EVENT_BY_CODE -> performTransaction(FilmEventEntry.TABLE_NAME, "${FilmEventEntry.COLUMN_CODE} = ?", Array(1) { uri.lastPathSegment })
            CODE_PERFORMANCES -> performTransaction(PerformanceEntry.TABLE_NAME)
            else -> throw IllegalArgumentException("No match found for uri $uri")
        }
    }

    override fun getType(uri: Uri): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val CODE_FILM_EVENTS = 100
        const val CODE_FILM_EVENT_BY_CODE = 101
        const val CODE_PERFORMANCES = 200
        const val CODE_PERFORMANCES_BY_FILM_EVENT_CODE = 201

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(CONTENT_AUTHORITY, PATH_FILM_EVENTS, CODE_FILM_EVENTS)
            addURI(CONTENT_AUTHORITY, PATH_FILM_EVENT_BY_ID, CODE_FILM_EVENT_BY_CODE)
            addURI(CONTENT_AUTHORITY, PATH_PERFORMANCES, CODE_PERFORMANCES)
            addURI(CONTENT_AUTHORITY, PATH_PERFORMANCE_BY_FILM_EVENT_CODE, CODE_PERFORMANCES_BY_FILM_EVENT_CODE)
        }
    }
}