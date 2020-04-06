package dev.lunarcoffee.indigo.bot.util.settings.usersettings

import java.time.ZoneId

data class UserSettings(val userId: String, val zone: ZoneId? = null)
