package dev.lunarcoffee.indigo.framework.api.dsl

import dev.lunarcoffee.indigo.framework.core.bot.GuildCommandBot
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandExecutor
import net.dv8tion.jda.api.JDABuilder

class GuildCommandBotDsl {
    lateinit var token: String
    lateinit var prefix: (String) -> List<String>

    fun build(start: Boolean): GuildCommandBot {
        val jda = JDABuilder(token)
        val executor = GuildCommandExecutor(prefix)

        return GuildCommandBot(jda, executor).apply {
            if (start)
                start()
        }
    }
}

fun bot(start: Boolean, init: GuildCommandBotDsl.() -> Unit) = GuildCommandBotDsl().apply(init).build(start)
