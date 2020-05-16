/**
 * This command implementation by Mojang.
 * And decompiled with idea source code was converted
 * to kotlin code.
 * Also added some logic, for example checking on
 * permissions, and for some commands shorten aliases.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
 */

package com.mairwunnx.projectessentials.core.impl.vanilla.commands


import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent

internal object SaveOffCommand : VanillaCommandBase() {
    private val SAVE_ALREADY_OFF_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.save.alreadyOff")
    )

    private var aliases =
        configuration.take().aliases.saveall + "save-off"

    private fun tryAssignAliases() {
        CommandAliases.aliases["save-off"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("save-off")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).executes { p_198618_0_ ->
                    checkPermissions(p_198618_0_.source)
                    val commandsource = p_198618_0_.source
                    var flag = false
                    for (serverworld in commandsource.server.worlds) {
                        if (serverworld != null && !serverworld.disableLevelSaving) {
                            serverworld.disableLevelSaving = true
                            flag = true
                        }
                    }
                    if (!flag) {
                        throw SAVE_ALREADY_OFF_EXCEPTION.create()
                    } else {
                        commandsource.sendFeedback(
                            TranslationTextComponent("commands.save.disabled"),
                            true
                        )
                        return@executes 1
                    }
                }
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.save.off", 4)) {
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        generalConfiguration.getBool(SETTING_LOC_ENABLED),
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                "native.command.restricted_hover",
                                "native.save.off", "4"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }
}
