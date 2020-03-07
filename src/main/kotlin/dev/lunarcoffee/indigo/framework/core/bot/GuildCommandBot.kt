package dev.lunarcoffee.indigo.framework.core.bot

import dev.lunarcoffee.indigo.framework.core.bot.config.DefaultBotConfig
import dev.lunarcoffee.indigo.framework.core.bot.loaders.CommandGroupLoader
import dev.lunarcoffee.indigo.framework.core.bot.loaders.EventListenerLoader
import dev.lunarcoffee.indigo.framework.core.commands.CommandExecutor
import dev.lunarcoffee.indigo.framework.core.services.paginators.PaginatorReactionListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.yaml.snakeyaml.Yaml
import java.io.File

class GuildCommandBot(
    private val jdaBuilder: JDABuilder,
    override val commandExecutor: CommandExecutor,
    configPath: String
) : CommandBot, ListenerBot {

    override lateinit var jda: JDA

    override val config = DefaultBotConfig(Yaml().load(File(configPath).readText()))

    override val commandGroups = CommandGroupLoader(config.sourceRoot).load()
    override val commands = commandGroups.values.flatten()
    override val commandsByName = commands.flatMap { c -> c.names.map { it to c } }.toMap()

    override val listeners = EventListenerLoader(config.sourceRoot).load()

    override fun start() {
        commandExecutor.bot = this
        jda = jdaBuilder
            .setToken(config.token)
            .addEventListeners(*listeners.toTypedArray(), commandExecutor, PaginatorReactionListener)
            .build()
    }
}
