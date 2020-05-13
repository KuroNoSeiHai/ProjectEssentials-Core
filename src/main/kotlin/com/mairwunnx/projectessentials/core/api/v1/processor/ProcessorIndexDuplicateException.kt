package com.mairwunnx.projectessentials.core.api.v1.processor

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty

/**
 * Throws when processor with same load index already exist.
 * @param message exception message.
 * @since 2.0.0-SNAPSHOT.1.
 */
class ProcessorIndexDuplicateException(
    message: String = String.empty
) : Exception(message)
