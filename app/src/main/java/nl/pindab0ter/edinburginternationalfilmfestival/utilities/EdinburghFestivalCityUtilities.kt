package nl.pindab0ter.edinburginternationalfilmfestival.utilities

import android.content.Context
import android.net.Uri
import nl.pindab0ter.edinburginternationalfilmfestival.R
import java.net.URL
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object EdinburghFestivalCityUtilities {
    fun buildUrl(context: Context): URL = with(context) {
        val baseUrl = getString(R.string.edinburgh_festival_city_base_url)
        val basePath = getString(R.string.edinburgh_festival_city_base_path)
        val festivalKey = getString(R.string.edinburgh_festival_city_festival_key)
        val festivalValue = getString(R.string.edinburgh_festival_city_festival_value)
        val apiKeyKey = getString(R.string.edinburgh_festival_city_api_key_key)
        val apiKeyValue = getString(R.string.edinburgh_festival_city_api_key_value)
        val signatureKey = getString(R.string.edinburgh_festival_city_signing_key)

        val unsignedUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(basePath)
                .appendQueryParameter(festivalKey, festivalValue)
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

    private fun generateSignatureNew(cryptoAlgorithm: String, unsignedQuery: String, signingKey: String): String {
        val mac = Mac.getInstance(cryptoAlgorithm)
        mac.init(SecretKeySpec(signingKey.toByteArray(), cryptoAlgorithm))
        return mac.doFinal(unsignedQuery.toByteArray()).joinToString("") { String.format("%02x", it) }
    }
}
