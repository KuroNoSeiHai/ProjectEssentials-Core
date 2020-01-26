/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ParticleArgument
import net.minecraft.command.arguments.Vec3Argument
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.particles.IParticleData
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import org.apache.logging.log4j.LogManager

internal object ParticleCommand {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.particle.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.particle + "particle"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["particle"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/particle` vanilla command")
        NativeCommandUtils.removeCommand("particle")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "name", ParticleArgument.particle()
                    ).executes { p_198562_0_ ->
                        spawnParticle(
                            p_198562_0_.source,
                            ParticleArgument.getParticle(p_198562_0_, "name"),
                            p_198562_0_.source.pos,
                            Vec3d.ZERO,
                            0.0f,
                            0,
                            false,
                            p_198562_0_.source.server.playerList.players
                        )
                    }.then(
                        Commands.argument(
                            "pos", Vec3Argument.vec3()
                        ).executes { p_201226_0_ ->
                            spawnParticle(
                                p_201226_0_.source,
                                ParticleArgument.getParticle(p_201226_0_, "name"),
                                Vec3Argument.getVec3(p_201226_0_, "pos"),
                                Vec3d.ZERO,
                                0.0f,
                                0,
                                false,
                                p_201226_0_.source.server.playerList.players
                            )
                        }.then(
                            Commands.argument(
                                "delta", Vec3Argument.vec3(false)
                            ).then(
                                Commands.argument(
                                    "speed", FloatArgumentType.floatArg(0.0f)
                                ).then(
                                    Commands.argument(
                                        "count", IntegerArgumentType.integer(0)
                                    ).executes { p_198565_0_ ->
                                        spawnParticle(
                                            p_198565_0_.source,
                                            ParticleArgument.getParticle(p_198565_0_, "name"),
                                            Vec3Argument.getVec3(p_198565_0_, "pos"),
                                            Vec3Argument.getVec3(p_198565_0_, "delta"),
                                            FloatArgumentType.getFloat(p_198565_0_, "speed"),
                                            IntegerArgumentType.getInteger(p_198565_0_, "count"),
                                            false,
                                            p_198565_0_.source.server.playerList.players
                                        )
                                    }.then(
                                        Commands.literal("force").executes { p_198561_0_ ->
                                            spawnParticle(
                                                p_198561_0_.source,
                                                ParticleArgument.getParticle(p_198561_0_, "name"),
                                                Vec3Argument.getVec3(p_198561_0_, "pos"),
                                                Vec3Argument.getVec3(p_198561_0_, "delta"),
                                                FloatArgumentType.getFloat(p_198561_0_, "speed"),
                                                IntegerArgumentType.getInteger(
                                                    p_198561_0_, "count"
                                                ),
                                                true,
                                                p_198561_0_.source.server.playerList.players
                                            )
                                        }.then(
                                            Commands.argument(
                                                "viewers", EntityArgument.players()
                                            ).executes { p_198566_0_ ->
                                                spawnParticle(
                                                    p_198566_0_.source,
                                                    ParticleArgument.getParticle(
                                                        p_198566_0_, "name"
                                                    ),
                                                    Vec3Argument.getVec3(p_198566_0_, "pos"),
                                                    Vec3Argument.getVec3(p_198566_0_, "delta"),
                                                    FloatArgumentType.getFloat(
                                                        p_198566_0_, "speed"
                                                    ),
                                                    IntegerArgumentType.getInteger(
                                                        p_198566_0_, "count"
                                                    ),
                                                    true,
                                                    EntityArgument.getPlayers(
                                                        p_198566_0_, "viewers"
                                                    )
                                                )
                                            }
                                        )
                                    ).then(
                                        Commands.literal("normal").executes { p_198560_0_ ->
                                            spawnParticle(
                                                p_198560_0_.source,
                                                ParticleArgument.getParticle(p_198560_0_, "name"),
                                                Vec3Argument.getVec3(p_198560_0_, "pos"),
                                                Vec3Argument.getVec3(p_198560_0_, "delta"),
                                                FloatArgumentType.getFloat(p_198560_0_, "speed"),
                                                IntegerArgumentType.getInteger(
                                                    p_198560_0_, "count"
                                                ),
                                                false,
                                                p_198560_0_.source.server.playerList.players
                                            )
                                        }.then(
                                            Commands.argument(
                                                "viewers", EntityArgument.players()
                                            ).executes { p_198567_0_: CommandContext<CommandSource> ->
                                                spawnParticle(
                                                    p_198567_0_.source,
                                                    ParticleArgument.getParticle(
                                                        p_198567_0_, "name"
                                                    ),
                                                    Vec3Argument.getVec3(p_198567_0_, "pos"),
                                                    Vec3Argument.getVec3(p_198567_0_, "delta"),
                                                    FloatArgumentType.getFloat(
                                                        p_198567_0_, "speed"
                                                    ),
                                                    IntegerArgumentType.getInteger(
                                                        p_198567_0_, "count"
                                                    ),
                                                    false,
                                                    EntityArgument.getPlayers(
                                                        p_198567_0_, "viewers"
                                                    )
                                                )
                                            }
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.particle", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "particle")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, TranslationTextComponent(
                                    "native.command.restricted_hover",
                                    "native.particle", "2"
                                )
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun spawnParticle(
        source: CommandSource,
        particleData: IParticleData,
        pos: Vec3d,
        delta: Vec3d,
        speed: Float,
        count: Int,
        force: Boolean,
        viewers: Collection<ServerPlayerEntity>
    ): Int {
        checkPermissions(source)

        var i = 0
        for (serverplayerentity in viewers) {
            if (source.world.spawnParticle(
                    serverplayerentity,
                    particleData,
                    force,
                    pos.x, pos.y, pos.z,
                    count,
                    delta.x, delta.y, delta.z,
                    speed.toDouble()
                )
            ) ++i
        }
        return if (i == 0) {
            throw FAILED_EXCEPTION.create()
        } else {
            @Suppress("DEPRECATION")
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.particle.success",
                    Registry.PARTICLE_TYPE.getKey(particleData.type).toString()
                ), true
            )
            i
        }
    }
}
