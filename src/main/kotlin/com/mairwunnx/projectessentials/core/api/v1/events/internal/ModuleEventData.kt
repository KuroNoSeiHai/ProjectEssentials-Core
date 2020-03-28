package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import com.mairwunnx.projectessentials.core.api.v1.module.IModule

/**
 * Domain event data, stores domain name (package path).
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class ModuleEventData(val module: IModule) : IModuleEventData