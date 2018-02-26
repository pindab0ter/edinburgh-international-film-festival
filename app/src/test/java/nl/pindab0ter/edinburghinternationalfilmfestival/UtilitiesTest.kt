package nl.pindab0ter.edinburghinternationalfilmfestival

import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.formatForDatabase
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.databaseStringToDate
import org.junit.Test

import org.junit.Assert.*
import java.util.*

class UtilitiesTest {
    @Test
    fun parseDate() {
        val date = Calendar.getInstance().apply { set(2015, 5, 19, 20, 35, 0) }.time
        assertEquals(databaseStringToDate("2015-06-19 20:35:00")!!.time / 1000, date.time / 1000)
    }

    @Test
    fun formatDate() {
        val date = Calendar.getInstance().apply { set(2015, 5, 19, 20, 35, 0) }.time
        assertEquals(date.formatForDatabase(), "2015-06-19 20:35:00")
    }
}
