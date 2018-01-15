package nl.pindab0ter.edinburghinternationalfilmfestival.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.RequestQueueHolder

class ImageFetcher(
        private val context: Context
) {
    private val logTag = ImageFetcher::class.simpleName

    fun fetch(url: String, listener: (bitmap: Bitmap) -> Unit) {
        val listImageRequest = ImageRequest(url, listener, 0, 0,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.ARGB_8888,
                { error: VolleyError? ->
                    Log.e(logTag, "$error")
                }
        )

        RequestQueueHolder.getInstance(context).add(listImageRequest)
    }
}