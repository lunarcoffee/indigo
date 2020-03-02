package dev.lunarcoffee.indigo.framework.core.bot.config

interface BotConfig {
    operator fun get(key: String): String?
}
