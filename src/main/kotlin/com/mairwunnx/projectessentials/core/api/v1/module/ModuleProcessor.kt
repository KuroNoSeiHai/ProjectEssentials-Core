package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.api.v1.MODULE_PROCESSOR_INDEX
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.*
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

@OptIn(ExperimentalUnsignedTypes::class)
internal object ModuleProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("MODULE PROCESSOR")
    private var modules = listOf<IModule>()

    fun getModules() = modules

    override val processorLoadIndex = MODULE_PROCESSOR_INDEX
    override val processorName = "module"

    override fun initialize() = Unit

    override fun process() {
        logger.info(marker, "Finding and processing modules")

        ProviderAPI.getProvidersByType(ProviderType.MODULE).forEach {
            if (isModule(it)) {
                val clazz = it.createInstance() as IModule

                ModuleEventAPI.fire(OnModuleClassProcessing, ModuleEventData(clazz))

                processIndexes(
                    clazz.getModuleData().loadIndex
                )

                logger.info(
                    marker,
                    "\n\n    *** Module found! ${it.simpleName}".plus(
                        "\n\n  - Name: ${clazz.getModule().getModuleData().name}"
                    ).plus(
                        "\n  - Class: ${it.qualifiedName}"
                    ).plus(
                        "\n  - Version: ${clazz.getModule().getModuleData().version}"
                    ).plus(
                        "\n  - API Version: ${clazz.getModule().getModuleData().apiVersion}\n\n"
                    )
                )
                modules = modules + clazz

                ModuleEventAPI.fire(OnModuleClassProcessed, ModuleEventData(clazz))
            }
        }
        sortByLoadIndex()
    }

    private fun processIndexes(index: UInt) {
        modules.forEach {
            if (it.getModule().getModuleData().loadIndex == index) {
                throw ModuleIndexDuplicateException(
                    "Module with same load index $index already processed."
                )
            }
        }
    }

    private fun isModule(kclazz: KClass<*>) = kclazz.isSubclassOf(IModule::class)

    private fun sortByLoadIndex() {
        modules = modules.sortedWith(compareBy {
            it.getModule().getModuleData().loadIndex
        })
    }

    override fun postProcess() {
        getModules().forEach {
            ModuleEventAPI.fire(OnModuleClassPostProcessing, ModuleEventData(it))
            logger.info(marker, "Starting initializing module ${it.getModuleData().name}")
            it.getModule().init()
        }
    }
}
