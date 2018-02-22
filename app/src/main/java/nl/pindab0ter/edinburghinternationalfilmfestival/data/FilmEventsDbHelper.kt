package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.FilmEventsContract.PerformanceEntry

class FilmEventsDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(sqlCreateWeatherTable)
        db?.execSQL(sqlCreatePerformanceTable)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${FilmEventEntry.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${PerformanceEntry.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "film_events.db"
        const val DATABASE_VERSION = 5

        // TODO: Save images as BLOB
        val sqlCreateWeatherTable = """
            |CREATE TABLE ${FilmEventEntry.TABLE_NAME} (
            |   ${FilmEventEntry.COLUMN_CODE}                VARCHAR PRIMARY KEY,
            |   ${FilmEventEntry.COLUMN_TITLE}               VARCHAR NOT NULL,
            |   ${FilmEventEntry.COLUMN_IMAGE_THUMBNAIL_URL} VARCHAR,
            |   ${FilmEventEntry.COLUMN_IMAGE_ORIGINAL_URL}  VARCHAR,
            |   ${FilmEventEntry.COLUMN_UPDATED}             DATE NOT NULL
            |);""".trimMargin()

        val sqlCreatePerformanceTable = """
            |CREATE TABLE ${PerformanceEntry.TABLE_NAME} (
            |   ${PerformanceEntry.COLUMN_ID}               INTEGER PRIMARY KEY AUTOINCREMENT,
            |   ${PerformanceEntry.COLUMN_FILM_EVENT_CODE}  VARCHAR NOT NULL,
            |   ${PerformanceEntry.COLUMN_START}            DATE NOT NULL,
            |   ${PerformanceEntry.COLUMN_END}              DATE NOT NULL,
            |   ${PerformanceEntry.COLUMN_PRICE}            INTEGER
            |);
            """.trimMargin()
    }
}

/*
|  ${FilmEventEntry.COLUMN_URL}          TEXT NULL,
|  ${FilmEventEntry.COLUMN_WEBSITE}      TEXT NULL,
|  ${FilmEventEntry.COLUMN_YEAR}         TEXT NULL,
|  ${FilmEventEntry.COLUMN_IMAGES}       TEXT NULL,
|  ${FilmEventEntry.COLUMN_VENUE}        TEXT NULL,
|  ${FilmEventEntry.COLUMN_DESCRIPTION}  TEXT NULL,
|  ${FilmEventEntry.COLUMN_GENRE}        TEXT NULL,
|  ${FilmEventEntry.COLUMN_GENRE_TAGS}   TEXT NULL,
|  ${FilmEventEntry.COLUMN_LATITUDE}     TEXT NULL,
|  ${FilmEventEntry.COLUMN_LONGITUDE}    TEXT NULL,
|  ${FilmEventEntry.COLUMN_STATUS}       TEXT NULL,
*/
