package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import java.text.DateFormat
import java.util.*

fun formatShowDate(date: Date?): String = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(date)
