package com.mairwunnx.projectessentials.core.api.v1.module

import com.mairwunnx.projectessentials.core.extensions.empty

/**
 * Throws when module not found.
 * @param message exception message.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class ModuleNotFoundException(
    message: String = String.empty
) : Exception(message)
