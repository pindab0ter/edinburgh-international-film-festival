package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.content.Context
import android.net.Uri
import android.util.Log
import nl.pindab0ter.edinburghinternationalfilmfestival.R
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.round

fun buildUrl(context: Context): URL = with(context) {
    val baseUrl = getString(R.string.edinburgh_festival_city_base_url)
    val basePath = getString(R.string.edinburgh_festival_city_base_path)
    val festivalKey = getString(R.string.edinburgh_festival_city_festival_key)
    val festivalValue = getString(R.string.edinburgh_festival_city_festival_value)
    val yearKey = getString(R.string.edinburgh_festival_city_year_key)
    val yearValue = getString(R.string.edinburgh_festival_city_year_value)
    val apiKeyKey = getString(R.string.edinburgh_festival_city_api_key_key)
    val apiKeyValue = getString(R.string.edinburgh_festival_city_api_key_value)
    val signatureKey = getString(R.string.edinburgh_festival_city_signing_key)

    val unsignedUri = Uri.parse(baseUrl).buildUpon()
            .appendPath(basePath)
            .appendQueryParameter(festivalKey, festivalValue)
            .appendQueryParameter(yearKey, yearValue)
            .appendQueryParameter(apiKeyKey, apiKeyValue)
            .build()

    val signatureValue = generateSignatureNew(
            cryptoAlgorithm = getString(R.string.crypto_algorithm),
            unsignedQuery = with(unsignedUri) { "$path?$query" },
            signingKey = getString(R.string.edinburgh_festival_city_secret_signing_key)
    )

    val signedUri = unsignedUri.buildUpon()
            .appendQueryParameter(signatureKey, signatureValue)
            .build()

    return URL(signedUri.toString())
}

private fun generateSignatureNew(cryptoAlgorithm: String, unsignedQuery: String, signingKey: String): String = with(Mac.getInstance(cryptoAlgorithm)) {
    init(SecretKeySpec(signingKey.toByteArray(), cryptoAlgorithm))
    doFinal(unsignedQuery.toByteArray()).joinToString("") { String.format("%02x", it) }
}


private val dateFormatForDatabase = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
private val dateFormatForDisplayShort = SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault())
private val dateFormatForDisplayLong = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
fun Date.formatForDisplayLong(): String = dateFormatForDisplayLong.format(this).capitalize()
fun Date.formatForDisplayShort(): String = dateFormatForDisplayShort.format(this).capitalize()
fun Date.formatForDatabase(): String = dateFormatForDatabase.format(this)
fun databaseStringToDate(string: String?): Date? = dateFormatForDatabase.parse(string)

fun starRatingFor(value: Float): String = StringBuilder().apply {
    require(value in 0.0..5.0, { "A star rating must be between 0.0 and 5.0" })

    fun Float.roundToNearestHalf(): Float = round(this * 2.0f) / 2.0f
    fun Float.roundsToHalf(): Boolean = roundToNearestHalf() % 1 == 0.5f
    fun Float.roundsUpToWhole(): Boolean = (this % 1).roundToNearestHalf() == 1.0f

    for (it in 0 until value.toInt()) append("★")
    if (value.roundsUpToWhole()) append("★")
    if (value.roundsToHalf()) append("½")
}.toString()

object LongLog {
    private const val maxLength = 1000

    fun d(tag: String?, message: String?) {
        if (message!!.length > maxLength) {
            Log.d(tag, message.substring(0 until maxLength))
            d(tag, message.substring(maxLength))
        } else Log.d(tag, message)
    }
}