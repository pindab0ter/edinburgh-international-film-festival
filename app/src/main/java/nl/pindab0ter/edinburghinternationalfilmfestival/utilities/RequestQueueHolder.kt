package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.extensions.SingletonHolder

object RequestQueueHolder : SingletonHolder<RequestQueue, Context>({
    Volley.newRequestQueue(it.applicationContext)
})
