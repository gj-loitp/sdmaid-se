package eu.darken.sdmse.common.dataarea.modules.pubdata

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import eu.darken.sdmse.common.dataarea.DataArea
import eu.darken.sdmse.common.dataarea.DataAreaType
import eu.darken.sdmse.common.dataarea.DeviceEnvironment
import eu.darken.sdmse.common.dataarea.modules.DataAreaModule
import eu.darken.sdmse.common.debug.Bugs
import eu.darken.sdmse.common.debug.logging.Logging.Priority.VERBOSE
import eu.darken.sdmse.common.debug.logging.log
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.user.UserManager2
import javax.inject.Inject

@Reusable
class SdcardsModule @Inject constructor(
    private val deviceEnvironment: DeviceEnvironment,
    private val userManager2: UserManager2,
) : DataAreaModule {

    override suspend fun firstPass(): Collection<DataArea> {
        val sdcards = mutableSetOf<DataArea>()

        // We can't scan /storage/emulated with root for multi user sdcards, because the paths might not be visible for root users.
        // TODO we are not getting multiuser sdcards
        deviceEnvironment.getPublicPrimaryStorage(userManager2.currentUser)
            .let {
                DataArea(
                    path = it.localPath,
                    type = DataAreaType.SDCARD,
                    userHandle = userManager2.currentUser,
                    flags = setOf(DataArea.Flag.PRIMARY),
                )
            }
            .run { sdcards.add(this) }

        deviceEnvironment.getPublicSecondaryStorage(userManager2.currentUser)
            .map {
                DataArea(
                    path = it.localPath,
                    type = DataAreaType.SDCARD,
                    userHandle = userManager2.currentUser,
                    flags = setOf(DataArea.Flag.SECONDARY),
                )
            }
            .run { sdcards.addAll(this) }

        if (sdcards.isEmpty()) Bugs.report(IllegalStateException("No sdcards found."))

        log(TAG, VERBOSE) { "firstPass():$sdcards" }

        return sdcards
    }

    override suspend fun secondPass(firstPass: Collection<DataArea>): Collection<DataArea> = firstPass

    @InstallIn(SingletonComponent::class)
    @Module
    abstract class DIM {
        @Binds @IntoSet abstract fun sync(mod: SdcardsModule): DataAreaModule
    }

    companion object {
        val TAG: String = logTag("DataArea", "Module", "Sdcard")
    }
}