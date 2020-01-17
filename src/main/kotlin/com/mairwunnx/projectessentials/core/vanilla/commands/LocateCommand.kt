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
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.ResourceLocationArgument
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import net.minecraftforge.registries.GameData
import org.apache.logging.log4j.LogManager

object LocateCommand {
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.locate.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.locate + "locate"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["locate"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/locate` vanilla command")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("Pillager_Outpost").executes { p_198530_0_ ->
                        locateStructure(p_198530_0_.source, "Pillager_Outpost")
                    }
                ).then(
                    Commands.literal("Mineshaft").executes { p_198535_0_ ->
                        locateStructure(p_198535_0_.source, "Mineshaft")
                    }
                ).then(
                    Commands.literal("Mansion").executes { p_198527_0_ ->
                        locateStructure(p_198527_0_.source, "Mansion")
                    }
                ).then(
                    Commands.literal("Igloo").executes { p_198529_0_ ->
                        locateStructure(p_198529_0_.source, "Igloo")
                    }
                ).then(
                    Commands.literal("Desert_Pyramid").executes { p_198526_0_ ->
                        locateStructure(p_198526_0_.source, "Desert_Pyramid")
                    }
                ).then(
                    Commands.literal("Jungle_Pyramid").executes { p_198531_0_ ->
                        locateStructure(p_198531_0_.source, "Jungle_Pyramid")
                    }
                ).then(
                    Commands.literal("Swamp_Hut").executes { p_198525_0_ ->
                        locateStructure(p_198525_0_.source, "Swamp_Hut")
                    }
                ).then(
                    Commands.literal("Stronghold").executes { p_198532_0_ ->
                        locateStructure(p_198532_0_.source, "Stronghold")
                    }
                ).then(
                    Commands.literal("Monument").executes { p_202686_0_ ->
                        locateStructure(p_202686_0_.source, "Monument")
                    }
                ).then(
                    Commands.literal("Fortress").executes { p_202685_0_ ->
                        locateStructure(p_202685_0_.source, "Fortress")
                    }
                ).then(
                    Commands.literal("EndCity").executes { p_202687_0_ ->
                        locateStructure(p_202687_0_.source, "EndCity")
                    }
                ).then(
                    Commands.literal("Ocean_Ruin").executes { p_204104_0_ ->
                        locateStructure(p_204104_0_.source, "Ocean_Ruin")
                    }
                ).then(
                    Commands.literal("Buried_Treasure").executes { p_204297_0_ ->
                        locateStructure(p_204297_0_.source, "Buried_Treasure")
                    }
                ).then(
                    Commands.literal("Shipwreck").executes { p_204758_0_ ->
                        locateStructure(p_204758_0_.source, "Shipwreck")
                    }
                ).then(
                    Commands.literal("Village").executes { p_218858_0_ ->
                        locateStructure(p_218858_0_.source, "Village")
                    }
                ).then(
                    Commands.argument(
                        "structure_type",
                        ResourceLocationArgument.resourceLocation()
                    ).suggests { _, sb ->
                        ISuggestionProvider.suggest(
                            GameData.getStructureFeatures().keySet().stream().map {
                                it.toString()
                            }, sb
                        )
                    }.executes { ctx ->
                        locateStructure(
                            ctx.source, ctx.getArgument(
                                "structure_type",
                                ResourceLocation::class.java
                            ).toString().replace("minecraft:", "")
                        )
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.locate", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "locate")
                )
                throw CommandException(
                    TranslationTextComponent(
                        "native.locate.restricted"
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun locateStructure(source: CommandSource, structureName: String): Int {
        checkPermissions(source)

        val blockpos = BlockPos(source.pos)
        val blockpos1 = source.world.findNearestStructure(
            structureName, blockpos, CommandsConfigurationUtils.getConfig().locateFindRadius, false
        )
        return if (blockpos1 == null) {
            throw FAILED_EXCEPTION.create()
        } else {
            val i = MathHelper.floor(
                getDistance(blockpos.x, blockpos.z, blockpos1.x, blockpos1.z)
            )
            val itextcomponent = TextComponentUtils.wrapInSquareBrackets(
                TranslationTextComponent(
                    "chat.coordinates",
                    blockpos1.x,
                    "~",
                    blockpos1.z
                )
            ).applyTextStyle { p_211746_1_ ->
                p_211746_1_.setColor(
                    TextFormatting.GREEN
                ).setClickEvent(
                    ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        "/tp @s " + blockpos1.x + " ~ " + blockpos1.z
                    )
                ).hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    TranslationTextComponent("chat.coordinates.tooltip")
                )
            }
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.locate.success",
                    structureName,
                    itextcomponent,
                    i
                ), false
            )
            i
        }
    }

    private fun getDistance(x1: Int, z1: Int, x2: Int, z2: Int): Float {
        val i = x2 - x1
        val j = z2 - z1
        return MathHelper.sqrt((i * i + j * j).toFloat())
    }
}
