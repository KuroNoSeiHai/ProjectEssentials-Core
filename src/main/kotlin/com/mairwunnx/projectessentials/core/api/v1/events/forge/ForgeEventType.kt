package com.mairwunnx.projectessentials.core.api.v1.events.forge

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventType

/**
 * Some event types of forge, EventAPI.
 * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
 */
enum class ForgeEventType : IModuleEventType {
    /**
     * `setup` method for modloading.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    SetupEvent,

    /**
     * `enqueueIMC` method for modloading.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    EnqueueIMCEvent,

    /**
     * `processIMC` method for modloading.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    ProcessIMCEvent,

    /**
     * `doClientStuff` method for modloading.
     * @since Mod: 2.0.0-SNAPSHOT.1+MC-1.14.4, API: 1.0.0
     */
    DoClientStuffEvent
}
