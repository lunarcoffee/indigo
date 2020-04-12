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

    suspend fun <TArg> check(arg: TArg, failureMessage: String, check: TArg.() -> Boolean): Unit? {
        if (check(arg) && !checkFailed) {
            checkFailed = true
            failure(failureMessage)
            return null
        }
        return Unit
    }

    suspend fun <TArg> checkNull(arg: TArg, failureMessage: String) = check(arg, failureMessage) { this == null }
    suspend fun <TArg> checkNotNull(arg: TArg, failureMessage: String) = check(arg, failureMessage) { this != null }
}
