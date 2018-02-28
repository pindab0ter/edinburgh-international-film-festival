package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import nl.pindab0ter.edinburghinternationalfilmfestival.R

@GlideModule
open class AppGlideModuleImplementation : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(InternalCacheDiskCacheFactory(context))
        builder.setDefaultRequestOptions(RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .fallback(R.drawable.ic_broken_image)
                .error(R.drawable.ic_broken_image)
        )
    }
}