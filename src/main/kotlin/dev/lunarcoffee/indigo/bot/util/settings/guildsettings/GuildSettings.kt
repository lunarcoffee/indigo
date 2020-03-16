package dev.lunarcoffee.indigo.bot.util.settings.guildsettings

class GuildSettings(
    val guildId: String,
    var prefixes: List<String> = listOf(".."),
    var configRoleId: String? = null,
    var starboard: StarboardSettings = StarboardSettings(null, false, 1)
)
