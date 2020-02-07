package com.mairwunnx.projectessentials.core

import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.vanilla.commands.*
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager

@Suppress("unused")
@Mod("project_essentials_core")
internal class EntryPoint : EssBase() {
    private val logger = LogManager.getLogger()

    init {
        modInstance = this
        modVersion = "1.14.4-1.2.1"
        logBaseInfo()
        validateForgeVersion()
        MinecraftForge.EVENT_BUS.register(this)
        loadAdditionalModules()
        CommandsConfigurationUtils.loadConfig()
    }

    companion object {
        internal lateinit var modInstance: EntryPoint
        internal var permissionsInstalled: Boolean = false
        internal var cooldownInstalled: Boolean = false

        internal fun hasPermission(
            player: ServerPlayerEntity,
            node: String,
            opLevel: Int = 4
        ): Boolean = if (permissionsInstalled) {
            PermissionsAPI.hasPermission(player.name.string, node)
        } else {
            player.server.opPermissionLevel >= opLevel
        }
    }

    private fun loadAdditionalModules() {
        try {
            Class.forName(permissionAPIClassPath)
            permissionsInstalled = true
        } catch (_: ClassNotFoundException) {
            // ignored
        }

        try {
            Class.forName(cooldownAPIClassPath)
            cooldownInstalled = true
        } catch (_: ClassNotFoundException) {
            // ignored
        }
    }

    @SubscribeEvent
    internal fun onServerStarting(it: FMLServerStartingEvent) {
        if (CommandsConfigurationUtils.getConfig().nativeReplace) {
            NativeCommandUtils.assignDispatcherRoot(
                it.server.commandManager.dispatcher
            )
            registerNativeCommands(
                it.server.commandManager.dispatcher,
                it.server.isDedicatedServer
            )
        }
    }

    private fun registerNativeCommands(
        dispatcher: CommandDispatcher<CommandSource>,
        isDedicatedServer: Boolean
    ) {
        logger.info("Replacing native vanilla commands")
        AdvancementCommand.register(dispatcher)
        BossBarCommand.register(dispatcher)
        ClearCommand.register(dispatcher)
        CloneCommand.register(dispatcher)
        DataPackCommand.register(dispatcher)
        DebugCommand.register(dispatcher)
        DefaultGameModeCommand.register(dispatcher)
        DifficultyCommand.register(dispatcher)
        EffectCommand.register(dispatcher)
        EnchantCommand.register(dispatcher)
        ExecuteCommand.register(dispatcher)
        ExperienceCommand.register(dispatcher)
        FillCommand.register(dispatcher)
        ForceLoadCommand.register(dispatcher)
        FunctionCommand.register(dispatcher)
        GameModeCommand.register(dispatcher)
        GameRuleCommand.register(dispatcher)
        GiveCommand.register(dispatcher)
        HelpCommand.register(dispatcher)
        KickCommand.register(dispatcher)
        KillCommand.register(dispatcher)
        ListCommand.register(dispatcher)
        LocateCommand.register(dispatcher)
        LootCommand.register(dispatcher)
        MeCommand.register(dispatcher)
        MessageCommand.register(dispatcher)
        ParticleCommand.register(dispatcher)
        PlaySoundCommand.register(dispatcher)
        PublishCommand.register(dispatcher)
        RecipeCommand.register(dispatcher)
        ReloadCommand.register(dispatcher)
        ReplaceItemCommand.register(dispatcher)
        SayCommand.register(dispatcher)
        ScheduleCommand.register(dispatcher)
        ScoreboardCommand.register(dispatcher)
        SeedCommand.register(dispatcher)
        SetBlockCommand.register(dispatcher)
        SetWorldSpawnCommand.register(dispatcher)
        SpawnPointCommand.register(dispatcher)
        SpreadPlayersCommand.register(dispatcher)
        StopSoundCommand.register(dispatcher)
        SummonCommand.register(dispatcher)
        TagCommand.register(dispatcher)
        TeamCommand.register(dispatcher)
        TeamMsgCommand.register(dispatcher)
        TeleportCommand.register(dispatcher)
        TellRawCommand.register(dispatcher)
        TimeCommand.register(dispatcher)
        TitleCommand.register(dispatcher)
        TriggerCommand.register(dispatcher)
        WeatherCommand.register(dispatcher)
        WorldBorderCommand.register(dispatcher)

        if (isDedicatedServer) {
            logger.info("Replacing native vanilla server commands")
            BanCommand.register(dispatcher)
            BanIpCommand.register(dispatcher)
            BanListCommand.register(dispatcher)
            DeOpCommand.register(dispatcher)
            OpCommand.register(dispatcher)
            PardonCommand.register(dispatcher)
            PardonIpCommand.register(dispatcher)
            SaveAllCommand.register(dispatcher)
            SaveOffCommand.register(dispatcher)
            SaveOnCommand.register(dispatcher)
            SetIdleTimeoutCommand.register(dispatcher)
            StopCommand.register(dispatcher)
            WhitelistCommand.register(dispatcher)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onServerStopping(it: FMLServerStoppingEvent) {
        logger.info("Shutting down $modName mod ...")
        CommandsConfigurationUtils.saveConfig()
    }
}
