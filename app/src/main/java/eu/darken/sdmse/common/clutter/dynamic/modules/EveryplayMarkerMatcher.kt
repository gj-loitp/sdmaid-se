package eu.darken.sdmse.common.clutter.dynamic.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import eu.darken.sdmse.common.clutter.MarkerSource
import eu.darken.sdmse.common.clutter.dynamic.NestedPackageMatcher
import eu.darken.sdmse.common.storageareas.StorageArea
import java.util.*
import javax.inject.Inject

@Reusable
class EveryplayMarkerMatcher @Inject constructor() : NestedPackageMatcher(
    StorageArea.Type.SDCARD,
    ".EveryplayCache",
    setOf(".nomedia", "images", "videos")
) {
    override fun toString(): String = "EveryplayMarkerSource"

    @Module @InstallIn(SingletonComponent::class)
    abstract class DIM {
        @Binds @IntoSet abstract fun source(source: EveryplayMarkerMatcher): MarkerSource
    }
}