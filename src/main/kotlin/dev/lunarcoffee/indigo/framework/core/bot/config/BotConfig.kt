package dev.lunarcoffee.indigo.framework.core.bot.config

interface BotConfig {
    val token get() = get("token") ?: error("Please specify a `token` in the config!")
    val sourceRoot get() = get("sourceRoot") ?: error("Please specify a `sourceRoot` in the config!")

    operator fun get(key: String): String?
}
