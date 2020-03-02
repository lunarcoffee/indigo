package dev.lunarcoffee.indigo.framework.core.bot

import dev.lunarcoffee.indigo.framework.core.bot.config.BotConfig
import dev.lunarcoffee.indigo.framework.core.std.HasJDA

interface Bot : HasJDA {
    val config: BotConfig

    fun start()
}
