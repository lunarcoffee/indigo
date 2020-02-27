package dev.lunarcoffee.indigo.framework.core.bot

import dev.lunarcoffee.indigo.framework.core.bot.loaders.CommandGroupLoader
import dev.lunarcoffee.indigo.framework.core.bot.loaders.EventListenerLoader
import dev.lunarcoffee.indigo.framework.core.commands.CommandExecutor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener

class GuildCommandBot(
    private val jdaBuilder: JDABuilder,
    override val commandExecutor: CommandExecutor
) : CommandBot, ListenerBot {

    override lateinit var jda: JDA

    override val commandGroups = CommandGroupLoader().load()
    override val commandsByName = commandGroups.values.flatten().associateBy { it.name }
    override val commands = commandsByName.values.toList()

    // TODO: filter non-event listeners with annotation
    override val listeners = EventListenerLoader().load() + commandExecutor as EventListener

    override fun start() {
        commandExecutor.bot = this
        jda = jdaBuilder.addEventListeners(*listeners.toTypedArray()).build()
    }
}
