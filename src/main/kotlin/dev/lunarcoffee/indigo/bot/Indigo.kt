package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.bot.util.prefixes.PrefixManager
import dev.lunarcoffee.indigo.framework.api.dsl.bot

fun main() = bot("src/main/resources/config.yaml") { customPrefix(PrefixManager::getPrefix) }.start()
