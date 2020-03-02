package dev.lunarcoffee.indigo.framework.core.bot.config

class DefaultBotConfig(private val map: Map<String, String>) : BotConfig {
    override operator fun get(key: String) = map[key]
}
