package com.mairwunnx.projectessentials.core.api.v1.configuration

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when configuration not found.
 * @param message exception message.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
class ConfigurationNotFoundException(
    message: String = String.empty
) : Exception(message)
