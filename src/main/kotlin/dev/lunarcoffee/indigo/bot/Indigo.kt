package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.core.bot.GuildCommandBot
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandExecutor
import net.dv8tion.jda.api.JDABuilder

// FIXME: URGENT: regenerate token
fun main() {
    val jda = JDABuilder("NTY0MjM4MTMzMTUyMDU1MzE3.XlfQNQ.OFpPFXz69Qhw3c8XBamvMrfzcek")
    GuildCommandBot(jda, GuildCommandExecutor { ".." }).start()
}
