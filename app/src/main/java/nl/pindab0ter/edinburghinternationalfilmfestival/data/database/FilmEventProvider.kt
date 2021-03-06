package nl.pindab0ter.edinburghinternationalfilmfestival.data.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.CONTENT_AUTHORITY
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.PerformanceEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.PATH_FILM_EVENTS
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.PATH_FILM_EVENT_BY_CODE
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.PATH_PERFORMANCES
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.PATH_PERFORMANCES_BY_FILM_EVENT_CODE
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.PATH_PERFORMANCES_BY_ID

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
                    close()
                    Log.v(logTag, "Inserted film_event with code $code")
                    FilmEventEntry.CONTENT_URI.buildUpon()
                            .appendPath(code)
                            .build()
                } else {
                    endTransaction()
                    close()
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
                    close()
                    Log.v(logTag, "Inserted performance for film_event with code $filmEventCode")
                    PerformanceEntry.CONTENT_URI.buildUpon()
                            .appendPath(id.toString())
                            .build()
                } else {
                    endTransaction()
                    close()
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
        when (uriMatcher.match(uri)) {
            CODE_FILM_EVENTS -> query(FilmEventEntry.TABLE_NAME, null, null, null, null, null, null)
            CODE_FILM_EVENT_BY_CODE -> query(FilmEventEntry.TABLE_NAME, null, "${FilmEventEntry.COLUMN_CODE} = ?", arrayOf(uri.lastPathSegment), null, null, null)
            CODE_PERFORMANCES -> query(PerformanceEntry.TABLE_NAME, null, null, null, null, null, null)
            CODE_PERFORMANCES_BY_FILM_EVENT_CODE -> query(PerformanceEntry.TABLE_NAME, null, "${PerformanceEntry.COLUMN_FILM_EVENT_CODE} = ?", Array(1) { uri.lastPathSegment }, null, null, null)
            else -> throw IllegalArgumentException("No match found for uri $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = with(filmEventDbHelper.writableDatabase) {
        when (uriMatcher.match(uri)) {
            CODE_FILM_EVENT_BY_CODE -> {
                beginTransaction()
                val rowsAffected = update(FilmEventEntry.TABLE_NAME, values, "${FilmEventEntry.COLUMN_CODE} = ?", arrayOf(uri.lastPathSegment))
                if (rowsAffected > 0) setTransactionSuccessful()
                endTransaction()
                return rowsAffected
            }
            CODE_PERFORMANCES_BY_ID -> {
                beginTransaction()
                val rowsAffected = update(PerformanceEntry.TABLE_NAME, values, "${PerformanceEntry.COLUMN_ID} = ?", arrayOf(uri.lastPathSegment))
                if (rowsAffected > 0) setTransactionSuccessful()
                endTransaction()
                return rowsAffected
            }
            else -> throw IllegalArgumentException("No match found for uri $uri")
        }
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
        const val CODE_PERFORMANCES_BY_ID = 201
        const val CODE_PERFORMANCES_BY_FILM_EVENT_CODE = 202

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(CONTENT_AUTHORITY, PATH_FILM_EVENTS, CODE_FILM_EVENTS)
            addURI(CONTENT_AUTHORITY, PATH_FILM_EVENT_BY_CODE + "/#", CODE_FILM_EVENT_BY_CODE)
            addURI(CONTENT_AUTHORITY, PATH_PERFORMANCES, CODE_PERFORMANCES)
            addURI(CONTENT_AUTHORITY, PATH_PERFORMANCES_BY_ID + "/#", CODE_PERFORMANCES_BY_ID)
            addURI(CONTENT_AUTHORITY, PATH_PERFORMANCES_BY_FILM_EVENT_CODE + "/#", CODE_PERFORMANCES_BY_FILM_EVENT_CODE)
        }
    }
}