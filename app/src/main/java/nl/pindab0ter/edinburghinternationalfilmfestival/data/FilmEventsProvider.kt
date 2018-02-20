package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.CONTENT_AUTHORITY
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.PATH_FILM_EVENTS

class FilmEventsProvider : ContentProvider() {
    private lateinit var filmEventsDbHelper: FilmEventsDbHelper
    private val logTag = FilmEventsProvider::class.simpleName

    override fun onCreate(): Boolean {
        filmEventsDbHelper = FilmEventsDbHelper(context)
        return true
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri = with(filmEventsDbHelper.writableDatabase) {
        when (uriMatcher.match(uri)) {
            CODE_FILM_EVENTS -> {
                beginTransaction()
                val id = insert(FilmEventEntry.TABLE_NAME, null, values)
                setTransactionSuccessful()
                endTransaction()

                Log.v(logTag, "Inserted entry with id $id")

                require(id != -1L, { "Failed to insert record" })

                FilmEventsContract.BASE_CONTENT_URI.buildUpon()
                        .appendPath(id.toString())
                        .build()
            }
            else -> throw IllegalArgumentException("No match found for uri $uri")
        }
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor = with(filmEventsDbHelper.readableDatabase) {
        when (uriMatcher.match(uri)) {
            CODE_FILM_EVENTS -> {
                Log.v(logTag, "Query for $uri")
                beginTransaction()
                val cursor = query(FilmEventEntry.TABLE_NAME, null, null, null, null, null, null)
                endTransaction()

                return cursor
            }
            else -> throw IllegalArgumentException("No match found for uri $uri")
        }
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(uri: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val CODE_FILM_EVENTS = 100

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(CONTENT_AUTHORITY, PATH_FILM_EVENTS, CODE_FILM_EVENTS)
        }
    }
}