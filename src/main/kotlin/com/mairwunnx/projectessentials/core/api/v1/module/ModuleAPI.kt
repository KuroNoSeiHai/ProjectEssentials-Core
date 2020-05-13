package com.mairwunnx.projectessentials.core.api.v1.module

import net.minecraftforge.fml.common.Mod

/**
 * Class for interacting with other modules.
 * @since 2.0.0-SNAPSHOT.1.
 */
@Suppress("unused")
object ModuleAPI {
    /**
     * @return all installed and checked modules.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getAllModules() = ModuleProcessor.getModules()

    /**
     * @return module mod id what declared in `@Mod` annotation.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getModuleModId(module: IModule): String {
        if (module.getModuleData().name == "essentials") {
            return "project_essentials"
        }
        if (module.javaClass.isAnnotationPresent(Mod::class.java)) {
            return module.javaClass.getAnnotation(Mod::class.java).value
        }
        return "project_essentials_${module.getModuleData().name}"
    }

    /**
     * @return module by provided name.
     * @throws ModuleNotFoundException when module not found.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getModuleByName(name: String) =
        getAllModules().find { it.getModule().getModuleData().name == name }?.let {
            return@let it
        } ?: throw ModuleNotFoundException(
            "Module with name $name not found and not processed."
        )

    /**
     * @param module module class instance.
     * @return true if module existing or installed.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun isModuleExist(module: IModule) = ModuleProcessor.getModules().find {
        it.getModule().getModuleData().name == module.getModule().getModuleData().name
    }.let { return@let it != null }

    /**
     * @param module module name what provided in Module annotation.
     * @return true if module existing or installed.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun isModuleExist(module: String) = ModuleProcessor.getModules().find {
        it.getModule().getModuleData().name == module
    }.let { return@let it != null }
}
