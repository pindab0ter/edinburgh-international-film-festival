package nl.pindab0ter.edinburghinternationalfilmfestival.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.FilmEventEntry
import nl.pindab0ter.edinburghinternationalfilmfestival.data.database.FilmEventContract.PerformanceEntry

class FilmEventDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val logTag = FilmEventDbHelper::class.simpleName

    override fun onCreate(db: SQLiteDatabase?) {
        Log.v(logTag, "Creating database...")
        db?.execSQL(sqlCreateFilmEventTable)
        db?.execSQL(sqlCreatePerformanceTable)
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        dropAllTablesAndRecreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) = onUpgrade(db, oldVersion, newVersion)

    fun dropAllTables(db: SQLiteDatabase?) {
        Log.v(logTag, "Dropping all tables...")
        db?.execSQL("DROP TABLE IF EXISTS ${FilmEventEntry.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${PerformanceEntry.TABLE_NAME}")
    }

    fun dropAllTablesAndRecreate(db: SQLiteDatabase?) {
        dropAllTables(db)
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "film_festival.db"
        const val DATABASE_VERSION = 12

        val sqlCreateFilmEventTable = """
            |CREATE TABLE ${FilmEventEntry.TABLE_NAME} (
            |   ${FilmEventEntry.COLUMN_CODE}            VARCHAR PRIMARY KEY ON CONFLICT IGNORE,
            |   ${FilmEventEntry.COLUMN_TITLE}           VARCHAR NOT NULL,
            |   ${FilmEventEntry.COLUMN_DESCRIPTION}     VARCHAR,
            |   ${FilmEventEntry.COLUMN_GENRE_TAGS}      VARCHAR,
            |   ${FilmEventEntry.COLUMN_WEBSITE}         VARCHAR,
            |   ${FilmEventEntry.COLUMN_IMAGE_THUMBNAIL} VARCHAR,
            |   ${FilmEventEntry.COLUMN_IMAGE_ORIGINAL}  VARCHAR,
            |   ${FilmEventEntry.COLUMN_VENUE_NAME}      VARCHAR,
            |   ${FilmEventEntry.COLUMN_VENUE_ADDRESS}   VARCHAR,
            |   ${FilmEventEntry.COLUMN_UPDATED}         DATE NOT NULL
            |);""".trimMargin()

        val sqlCreatePerformanceTable = """
            |CREATE TABLE ${PerformanceEntry.TABLE_NAME} (
            |   ${PerformanceEntry.COLUMN_ID}               INTEGER PRIMARY KEY AUTOINCREMENT,
            |   ${PerformanceEntry.COLUMN_FILM_EVENT_CODE}  VARCHAR NOT NULL REFERENCES ${FilmEventEntry.TABLE_NAME}(${FilmEventEntry.COLUMN_CODE}) ON DELETE CASCADE,
            |   ${PerformanceEntry.COLUMN_START}            DATE NOT NULL,
            |   ${PerformanceEntry.COLUMN_END}              DATE NOT NULL,
            |   ${PerformanceEntry.COLUMN_SCHEDULED}        INTEGER DEFAULT 0,
            |   UNIQUE (${PerformanceEntry.COLUMN_FILM_EVENT_CODE}, ${PerformanceEntry.COLUMN_START}) ON CONFLICT IGNORE
            |);
            """.trimMargin()
    }
}
