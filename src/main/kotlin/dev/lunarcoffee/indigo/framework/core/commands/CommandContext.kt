package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.bot.util.failure
import dev.lunarcoffee.indigo.framework.core.std.HasBot
import dev.lunarcoffee.indigo.framework.core.std.HasJDA
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.GenericEvent

interface CommandContext : HasBot, HasJDA, TextChannel {
    val event: GenericEvent
    val invokedPrefix: String

    var checkFailed: Boolean

    suspend fun <T> check(arg: T, failureMessage: String, check: T.() -> Boolean): Unit? {
        if (check(arg) && !checkFailed) {
            checkFailed = true
            failure(failureMessage)
            return null
        }
        return Unit
    }

    suspend fun <T> checkNull(arg: T, failureMessage: String) = check(arg, failureMessage) { this == null }
}
