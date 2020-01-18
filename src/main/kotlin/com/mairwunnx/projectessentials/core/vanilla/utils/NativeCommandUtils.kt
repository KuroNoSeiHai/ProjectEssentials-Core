package com.mairwunnx.projectessentials.core.vanilla.utils

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.RootCommandNode
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

/**
 * @since 1.14.4-1.1.0.0
 */
object NativeCommandUtils {
    private val logger = LogManager.getLogger()
    private lateinit var registeredCommands: RootCommandNode<CommandSource>

    internal fun assignDispatcherRoot(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        logger.debug("Finding `root` field in `dispatcher` instance")
        val root = dispatcher.javaClass.getDeclaredField("root")
        logger.debug("Setting value `true` for property `isAccessible` of taken `root` instance")
        root.isAccessible = true
        logger.debug("Taking value of `root` as `RootCommandNode<CommandSource>`")
        @Suppress("UNCHECKED_CAST")
        registeredCommands = root.get(dispatcher) as RootCommandNode<CommandSource>
    }

    /**
     * Just remove vanilla or other registered command.
     * @param commandName command name to remove.
     * @since 1.14.4-1.1.0.0
     */
    @JvmStatic
    fun removeCommand(commandName: String) {
        logger.info("Removing native vanilla command /$commandName")
        registeredCommands.children.removeIf {
            it.name == commandName
        }
    }
}
