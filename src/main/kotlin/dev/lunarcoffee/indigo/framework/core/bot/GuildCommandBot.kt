package dev.lunarcoffee.indigo.framework.core.bot

import dev.lunarcoffee.indigo.framework.core.bot.config.BotConfig
import dev.lunarcoffee.indigo.framework.core.bot.config.DefaultBotConfig
import dev.lunarcoffee.indigo.framework.core.bot.loaders.CommandGroupLoader
import dev.lunarcoffee.indigo.framework.core.bot.loaders.EventListenerLoader
import dev.lunarcoffee.indigo.framework.core.commands.CommandExecutor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener

class GuildCommandBot(
    private val jdaBuilder: JDABuilder,
    override val commandExecutor: CommandExecutor,
    configPath: String
) : CommandBot, ListenerBot {

    override lateinit var jda: JDA

    override val commandGroups = CommandGroupLoader().load()
    override val commands = commandGroups.values.flatten()
    override val commandsByName = commands.flatMap { c -> c.names.map { it to c } }.toMap()

    override val listeners = EventListenerLoader().load() + commandExecutor as EventListener
    override val config = DefaultBotConfig()

    override fun start() {
        commandExecutor.bot = this
        jda = jdaBuilder.addEventListeners(*listeners.toTypedArray()).build()
    }
}
