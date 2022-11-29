//package eu.thedarken.sdmse.tools.storage.modules.system
//
//import android.os.Environment
//import eu.thedarken.sdmse.tools.forensics.Location
//import eu.thedarken.sdmse.tools.io.FileOpsHelper
//import eu.thedarken.sdmse.tools.io.JavaFile
//import eu.thedarken.sdmse.tools.io.SDMFile
//import eu.thedarken.sdmse.tools.storage.Storage
//import eu.thedarken.sdmse.tools.storage.modules.StorageFactory
//import eu.thedarken.sdmse.tools.storage.modules.StorageFactoryModule
//
//internal class SystemModule(storageFactory: StorageFactory) : StorageFactoryModule(storageFactory) {
//    public override fun build(storageMap: Map<Location, Collection<Storage>>): Collection<Storage> {
//        val file: SDMFile = JavaFile.build(Environment.getRootDirectory())
//        val mount = FileOpsHelper.findMount(mounts, file) ?: return emptySet()
//
//        return Storage(
//                location = Location.SYSTEM,
//                mount = mount,
//                flags = setOf(Storage.Flag.PRIMARY),
//                file = file
//        ).let { setOf(it) }
//    }
//}