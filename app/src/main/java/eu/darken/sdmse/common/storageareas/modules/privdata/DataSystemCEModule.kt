package eu.darken.sdmse.common.storageareas.modules.privdata

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import eu.darken.sdmse.common.debug.logging.Logging.Priority.INFO
import eu.darken.sdmse.common.debug.logging.Logging.Priority.WARN
import eu.darken.sdmse.common.debug.logging.log
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.files.core.APath
import eu.darken.sdmse.common.files.core.GatewaySwitch
import eu.darken.sdmse.common.files.core.local.LocalGateway
import eu.darken.sdmse.common.files.core.local.LocalPath
import eu.darken.sdmse.common.storageareas.StorageArea
import eu.darken.sdmse.common.storageareas.hasFlags
import eu.darken.sdmse.common.storageareas.modules.DataAreaModule
import eu.darken.sdmse.common.user.UserManager2
import javax.inject.Inject

@Reusable
class DataSystemCEModule @Inject constructor(
    private val userManager2: UserManager2,
    private val gatewaySwitch: GatewaySwitch,
) : DataAreaModule {

    override suspend fun firstPass(): Collection<StorageArea> = emptySet()

    override suspend fun secondPass(firstPass: Collection<StorageArea>): Collection<StorageArea> {
        val gateway = gatewaySwitch.getGateway(APath.PathType.LOCAL) as LocalGateway

        if (!gateway.hasRoot()) {
            log(TAG, INFO) { "LocalGateway has no root, skipping." }
            return emptySet()
        }

        return firstPass
            .filter { it.type == StorageArea.Type.DATA && it.hasFlags(StorageArea.Flag.PRIMARY) }
            .map { area ->
                userManager2.allUsers.mapNotNull { userHandle ->
                    val path = LocalPath.build(area.path as LocalPath, "system_ce", userHandle.handleId.toString())

                    if (!gateway.exists(path, mode = LocalGateway.Mode.ROOT)) {
                        log(TAG, WARN) { "Does not exist: $path" }
                        return@mapNotNull null
                    }

                    StorageArea(
                        type = StorageArea.Type.DATA_SYSTEM_CE,
                        path = path,
                        userHandle = userHandle,
                        flags = area.flags,
                    )
                }
            }
            .flatten()
    }

    @Module @InstallIn(SingletonComponent::class)
    abstract class DIM {
        @Binds @IntoSet abstract fun mod(mod: DataSystemCEModule): DataAreaModule
    }

    companion object {
        val TAG: String = logTag("StorageArea", "Module", "DataSystemCE")
    }
}