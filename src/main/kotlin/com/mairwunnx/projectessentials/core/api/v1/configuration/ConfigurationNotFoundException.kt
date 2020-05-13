package com.mairwunnx.projectessentials.core.api.v1.configuration

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when configuration not found.
 * @param message exception message.
 * @since 2.0.0-SNAPSHOT.1.
 */
class ConfigurationNotFoundException(
    message: String = String.empty
) : Exception(message)
