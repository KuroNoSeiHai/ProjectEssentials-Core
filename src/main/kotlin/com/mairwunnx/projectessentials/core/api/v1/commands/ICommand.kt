package com.mairwunnx.projectessentials.core.api.v1.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

/**
 * Base interface for all command classes.
 * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
 */
interface ICommand {
    /**
     * Initialize command, assign data and other.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun initialize()

    /**
     * Register command.
     * @param dispatcher command dispatcher.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun register(dispatcher: CommandDispatcher<CommandSource>)

    /**
     * Process command, i.e execute command.
     * @param context command context.
     * @return int. Command execution result.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun process(context: CommandContext<CommandSource>): Int

    /**
     * @param clazz from what need take data.
     * @return Command annotation data class.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    fun getData(clazz: Class<*>): Command
}
