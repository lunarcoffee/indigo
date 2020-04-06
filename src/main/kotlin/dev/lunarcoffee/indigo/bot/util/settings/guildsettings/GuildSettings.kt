package dev.lunarcoffee.indigo.bot.util.settings.guildsettings

data class GuildSettings(
    val guildId: String,
    val prefixes: List<String> = listOf(".."),
    val configRoleId: String? = null,
    val starboard: StarboardSettings = StarboardSettings(null, false, 1)
)
