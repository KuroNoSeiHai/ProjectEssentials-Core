package com.mairwunnx.projectessentials.core.configuration.commands

import kotlinx.serialization.Serializable

@Serializable
internal data class CommandsConfiguration(
    var aliases: Aliases = Aliases()
) {
    @Serializable
    data class Aliases(
        var advancement: List<String> = listOf("achievements", "ac"),
        var ban: List<String> = listOf(),
        var banip: List<String> = listOf(),
        var banlist: List<String> = listOf()
    )
}
