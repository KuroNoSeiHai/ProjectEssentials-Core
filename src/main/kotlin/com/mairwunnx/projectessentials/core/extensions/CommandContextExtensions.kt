@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.extensions

import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity

/**
 * @return true if command sender is player.
 * @since 1.14.4-1.0.0.0
 */
fun CommandContext<CommandSource>.isPlayerSender(): Boolean =
    this.source.entity is ServerPlayerEntity

/**
 * @return player nickname from CommandContext.
 * @since 1.14.4-1.0.1.0
 */
fun CommandContext<CommandSource>.playerName(): String = this.source.asPlayer().name.string
