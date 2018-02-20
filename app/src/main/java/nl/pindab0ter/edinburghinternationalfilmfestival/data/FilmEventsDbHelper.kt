package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.FilmEventEntry

class FilmEventsDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(sqlCreateWeatherTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${FilmEventEntry.TABLE_NAME}")
    }

    companion object {
        const val DATABASE_NAME = "film_events.db"
        const val DATABASE_VERSION = 1

        val sqlCreateWeatherTable = """
            |  CREATE TABLE ${FilmEventEntry.TABLE_NAME} (
            |  ${FilmEventEntry._ID}                 INTEGER PRIMARY KEY AUTOINCREMENT,
            |  ${FilmEventEntry.COLUMN_CODE}         TEXT NULL,
            |  ${FilmEventEntry.COLUMN_TITLE}        TEXT NULL,
            |  UNIQUE (${FilmEventEntry.COLUMN_CODE}) ON CONFLICT REPLACE
            |);""".trimMargin()
    }
}

/*
|  ${FilmEventEntry.COLUMN_UPDATED}      TEXT NULL,
|  ${FilmEventEntry.COLUMN_URL}          TEXT NULL,
|  ${FilmEventEntry.COLUMN_WEBSITE}      TEXT NULL,
|  ${FilmEventEntry.COLUMN_YEAR}         TEXT NULL,
|  ${FilmEventEntry.COLUMN_IMAGES}       TEXT NULL,
|  ${FilmEventEntry.COLUMN_PERFORMANCES} TEXT NULL,
|  ${FilmEventEntry.COLUMN_VENUE}        TEXT NULL,
|  ${FilmEventEntry.COLUMN_DESCRIPTION}  TEXT NULL,
|  ${FilmEventEntry.COLUMN_GENRE}        TEXT NULL,
|  ${FilmEventEntry.COLUMN_GENRE_TAGS}   TEXT NULL,
|  ${FilmEventEntry.COLUMN_LATITUDE}     TEXT NULL,
|  ${FilmEventEntry.COLUMN_LONGITUDE}    TEXT NULL,
|  ${FilmEventEntry.COLUMN_STATUS}       TEXT NULL,
*/
