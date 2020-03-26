package dev.lunarcoffee.indigo.bot.commands.service

import dev.lunarcoffee.indigo.bot.commands.service.xkcd.XkcdComicRequester
import dev.lunarcoffee.indigo.bot.commands.service.xkcd.XkcdComicSender
import dev.lunarcoffee.indigo.bot.util.failureDefault
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrInt
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrWord
import kotlin.random.Random

@CommandGroup("Service")
class ServiceCommands {
    fun xkcd() = command("xkcd") {
        description = """
            |`$name [number|"r"|"random"]`
            |Gets you the latest, a specific, or random xkcd comic.
            |This command will give you the latest xkcd comic if no arguments are given (first example usage). If a
            |`number` is given, it will try to get the comic with that number. If `r` or `random` is given instead, I
            |will get you a random comic (including "comic" 404).
            |&{Example usage:}
            |- `$name`\n
            |- `$name 341`\n
            |- `$name r`\n
            |- `$name random`
        """.trimMargin()

        execute(TrInt.optional(), TrWord.optional()) { (number, random) ->
            val which = when {
                number != null && random == null -> number
                number == null && random != null && (random == "r" || random == "random") ->
                    Random.nextInt(XkcdComicRequester(null).get()!!.number) + 1
                number == null && random == null -> null
                else -> {
                    failureDefault(this@command.name)
                    return@execute
                }
            }
            send(XkcdComicSender(which))
        }
    }
}
