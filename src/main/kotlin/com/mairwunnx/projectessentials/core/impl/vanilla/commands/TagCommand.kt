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

import com.google.common.collect.Sets
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.Entity
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent

internal object TagCommand : VanillaCommandBase() {
    private val ADD_FAILED = SimpleCommandExceptionType(
        TranslationTextComponent("commands.tag.add.failed")
    )
    private val REMOVE_FAILED = SimpleCommandExceptionType(
        TranslationTextComponent("commands.tag.remove.failed")
    )

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("tag")

        dispatcher.register(
            Commands.literal("tag").then(
                Commands.argument(
                    "targets", EntityArgument.entities()
                ).then(
                    Commands.literal("add").then(
                        Commands.argument(
                            "name", StringArgumentType.word()
                        ).executes { p_198746_0_ ->
                            addTag(
                                p_198746_0_.source,
                                EntityArgument.getEntities(p_198746_0_, "targets"),
                                StringArgumentType.getString(p_198746_0_, "name")
                            )
                        }
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument(
                            "name", StringArgumentType.word()
                        ).suggests { p_198745_0_, p_198745_1_ ->
                            ISuggestionProvider.suggest(
                                getAllTags(
                                    EntityArgument.getEntities(p_198745_0_, "targets")
                                ), p_198745_1_
                            )
                        }.executes { p_198742_0_ ->
                            removeTag(
                                p_198742_0_.source,
                                EntityArgument.getEntities(p_198742_0_, "targets"),
                                StringArgumentType.getString(p_198742_0_, "name")
                            )
                        }
                    )
                ).then(
                    Commands.literal("list").executes { p_198747_0_ ->
                        listTags(
                            p_198747_0_.source,
                            EntityArgument.getEntities(p_198747_0_, "targets")
                        )
                    }
                )
            )
        )
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.tag", 2)) {
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
                                "native.tag", "2"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    /**
     * Gets all tags that are present on at least one of the given entities.
     */
    private fun getAllTags(entities: Collection<Entity>): Collection<String> {
        val set: MutableSet<String> = Sets.newHashSet()
        for (entity in entities) {
            set.addAll(entity.tags)
        }
        return set
    }

    @Throws(CommandSyntaxException::class)
    private fun addTag(
        source: CommandSource,
        entities: Collection<Entity>,
        tagName: String
    ): Int {
        checkPermissions(source)

        var i = 0
        for (entity in entities) {
            if (entity.addTag(tagName)) {
                ++i
            }
        }
        return if (i == 0) {
            throw ADD_FAILED.create()
        } else {
            if (entities.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.tag.add.success.single",
                        tagName,
                        entities.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.tag.add.success.multiple",
                        tagName,
                        entities.size
                    ), true
                )
            }
            i
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun removeTag(
        source: CommandSource,
        entities: Collection<Entity>,
        tagName: String
    ): Int {
        checkPermissions(source)

        var i = 0
        for (entity in entities) {
            if (entity.removeTag(tagName)) {
                ++i
            }
        }
        return if (i == 0) {
            throw REMOVE_FAILED.create()
        } else {
            if (entities.size == 1) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.tag.remove.success.single",
                        tagName,
                        entities.iterator().next().displayName
                    ), true
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.tag.remove.success.multiple",
                        tagName,
                        entities.size
                    ), true
                )
            }
            i
        }
    }

    private fun listTags(
        source: CommandSource,
        entities: Collection<Entity>
    ): Int {
        checkPermissions(source)

        val set: MutableSet<String> = Sets.newHashSet()
        for (entity in entities) {
            set.addAll(entity.tags)
        }
        if (entities.size == 1) {
            val entity1 = entities.iterator().next()
            if (set.isEmpty()) {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.tag.list.single.empty",
                        entity1.displayName
                    ), false
                )
            } else {
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.tag.list.single.success",
                        entity1.displayName,
                        set.size,
                        TextComponentUtils.makeGreenSortedList(set)
                    ), false
                )
            }
        } else if (set.isEmpty()) {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.tag.list.multiple.empty",
                    entities.size
                ), false
            )
        } else {
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.tag.list.multiple.success",
                    entities.size,
                    set.size,
                    TextComponentUtils.makeGreenSortedList(set)
                ), false
            )
        }
        return set.size
    }
}
