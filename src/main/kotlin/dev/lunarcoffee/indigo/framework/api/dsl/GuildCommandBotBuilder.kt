package dev.lunarcoffee.indigo.framework.api.dsl

import dev.lunarcoffee.indigo.framework.core.bot.GuildCommandBot
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandExecutor
import dev.lunarcoffee.indigo.framework.core.services.reloaders.ReloadableManager
import net.dv8tion.jda.api.JDABuilder

class GuildCommandBotDsl(private val configPath: String) {
    private lateinit var prefix: (String) -> List<String>

    fun singlePrefix(prefix: String) = customPrefix { listOf(prefix) }
    fun multiplePrefixes(vararg prefixes: String) = customPrefix { prefixes.toList() }

    fun customPrefix(prefixSelector: (String) -> List<String>) {
        prefix = prefixSelector
    }

    fun build(): GuildCommandBot {
        val jda = JDABuilder()
        val executor = GuildCommandExecutor(prefix)
        val reloadableManager = ReloadableManager()

        return GuildCommandBot(jda, reloadableManager, executor, configPath)
    }
}

fun bot(configPath: String, init: GuildCommandBotDsl.() -> Unit) = GuildCommandBotDsl(configPath).apply(init).build()
