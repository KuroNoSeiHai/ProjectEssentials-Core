package com.mairwunnx.projectessentials.core.api.v1.events.forge

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventData
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent

/**
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class InterModEnqueueEventData(val event: InterModEnqueueEvent) : IModuleEventData
