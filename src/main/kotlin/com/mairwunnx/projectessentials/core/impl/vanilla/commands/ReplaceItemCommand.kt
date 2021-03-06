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

import com.google.common.collect.Lists
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.BlockPosArgument
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.command.arguments.ItemArgument
import net.minecraft.command.arguments.SlotArgument
import net.minecraft.entity.Entity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import java.util.function.Function

internal object ReplaceItemCommand : VanillaCommandBase() {
    val BLOCK_FAILED_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.replaceitem.block.failed")
    )
    val INAPPLICABLE_SLOT_EXCEPTION = DynamicCommandExceptionType(
        Function { p_211409_0_: Any? ->
            TranslationTextComponent(
                "commands.replaceitem.slot.inapplicable",
                p_211409_0_
            )
        }
    )
    val ENTITY_FAILED_EXCEPTION = Dynamic2CommandExceptionType(
        Dynamic2CommandExceptionType.Function { p_211411_0_: Any?, p_211411_1_: Any? ->
            TranslationTextComponent(
                "commands.replaceitem.entity.failed",
                p_211411_0_,
                p_211411_1_
            )
        }
    )

    private var aliases =
        configuration.take().aliases.replaceItem + "replaceitem"

    private fun tryAssignAliases() {
        CommandAliases.aliases["replaceitem"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("replaceitem")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("block").then(
                        Commands.argument(
                            "pos", BlockPosArgument.blockPos()
                        ).then(
                            Commands.argument(
                                "slot", SlotArgument.slot()
                            ).then(
                                Commands.argument(
                                    "item", ItemArgument.item()
                                ).executes { p_198601_0_ ->
                                    replaceItemBlock(
                                        p_198601_0_.source,
                                        BlockPosArgument.getLoadedBlockPos(p_198601_0_, "pos"),
                                        SlotArgument.getSlot(p_198601_0_, "slot"),
                                        ItemArgument.getItem(
                                            p_198601_0_, "item"
                                        ).createStack(1, false)
                                    )
                                }.then(
                                    Commands.argument(
                                        "count", IntegerArgumentType.integer(1, 64)
                                    ).executes { p_198605_0_ ->
                                        replaceItemBlock(
                                            p_198605_0_.source,
                                            BlockPosArgument.getLoadedBlockPos(p_198605_0_, "pos"),
                                            SlotArgument.getSlot(p_198605_0_, "slot"),
                                            ItemArgument.getItem(
                                                p_198605_0_, "item"
                                            ).createStack(
                                                IntegerArgumentType.getInteger(
                                                    p_198605_0_, "count"
                                                ), true
                                            )
                                        )
                                    }
                                )
                            )
                        )
                    )
                ).then(
                    Commands.literal("entity").then(
                        Commands.argument(
                            "targets", EntityArgument.entities()
                        ).then(
                            Commands.argument(
                                "slot", SlotArgument.slot()
                            ).then(
                                Commands.argument(
                                    "item", ItemArgument.item()
                                ).executes { p_198600_0_ ->
                                    replaceItemEntities(
                                        p_198600_0_.source,
                                        EntityArgument.getEntities(p_198600_0_, "targets"),
                                        SlotArgument.getSlot(p_198600_0_, "slot"),
                                        ItemArgument.getItem(
                                            p_198600_0_, "item"
                                        ).createStack(1, false)
                                    )
                                }.then(
                                    Commands.argument(
                                        "count", IntegerArgumentType.integer(1, 64)
                                    ).executes { p_198606_0_ ->
                                        replaceItemEntities(
                                            p_198606_0_.source,
                                            EntityArgument.getEntities(p_198606_0_, "targets"),
                                            SlotArgument.getSlot(p_198606_0_, "slot"),
                                            ItemArgument.getItem(
                                                p_198606_0_, "item"
                                            ).createStack(
                                                IntegerArgumentType.getInteger(
                                                    p_198606_0_, "count"
                                                ), true
                                            )
                                        )
                                    }
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
            if (!hasPermission(source.asPlayer(), "native.replaceitem", 2)) {
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
                                "native.replaceitem", "2"
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
    private fun replaceItemBlock(
        source: CommandSource,
        pos: BlockPos,
        slotIn: Int,
        newStack: ItemStack
    ): Int {
        checkPermissions(source)

        val tileentity = source.world.getTileEntity(pos)
        return if (tileentity !is IInventory) {
            throw BLOCK_FAILED_EXCEPTION.create()
        } else {
            val iinventory = tileentity as IInventory
            if (slotIn >= 0 && slotIn < iinventory.sizeInventory) {
                iinventory.setInventorySlotContents(slotIn, newStack)
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.replaceitem.block.success",
                        pos.x, pos.y, pos.z,
                        newStack.textComponent
                    ), true
                )
                1
            } else {
                throw INAPPLICABLE_SLOT_EXCEPTION.create(slotIn)
            }
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun replaceItemEntities(
        source: CommandSource,
        targets: Collection<Entity>,
        slotIn: Int,
        newStack: ItemStack
    ): Int {
        checkPermissions(source)

        val list: MutableList<Entity> = Lists.newArrayListWithCapacity(targets.size)
        for (entity in targets) {
            if (entity is ServerPlayerEntity) {
                entity.container.detectAndSendChanges()
            }
            if (entity.replaceItemInInventory(slotIn, newStack.copy())) {
                list.add(entity)
                if (entity is ServerPlayerEntity) {
                    entity.container.detectAndSendChanges()
                }
            }
        }
        return if (list.isEmpty()) {
            throw ENTITY_FAILED_EXCEPTION.create(
                newStack.textComponent, slotIn
            )
        } else {
            if (list.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.replaceitem.entity.success.single",
                        list.iterator().next().displayName,
                        newStack.textComponent
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.replaceitem.entity.success.multiple",
                        list.size,
                        newStack.textComponent
                    ), true
                )
            }
            list.size
        }
    }
}
