package dev.lunarcoffee.indigo.bot.commands.service

import dev.lunarcoffee.indigo.bot.commands.service.lol.*
import dev.lunarcoffee.indigo.bot.commands.service.xkcd.XkcdComicRequester
import dev.lunarcoffee.indigo.bot.commands.service.xkcd.XkcdComicSender
import dev.lunarcoffee.indigo.bot.util.failureDefault
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import kotlin.random.Random

@CommandGroup("Service")
class ServiceCommands {
    fun xkcd() = command("xkcd") {
        description = """
            |`$name [number|"r"|"random"]`
            |Shows you the latest, a specific, or a random xkcd comic.
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

    fun lol() = command("lol", "lolinfo") {
        description = """
            |`$name <"champion"|"item"|"rune"> <champion name|item name|rune name>`
            |Shows information about a given champion or item in League of Legends.
            |This command will give you detailed information on a champion, item, or rune from League of Legends. If 
            |the first argument is `champion`, the next should be the `champion name`, similarly for the other two
            |options. These names do not have to be exact (no need to have perfect capitalization or spelling); if they 
            |are close enough, I can take a shot at guessing what you want.
            |&{Example usage:}
            |- `$name champion mordekaiser`\n
            |- `$name item zhonyas hourglass`\n
            |- `$name rune time warp tonic`
        """.trimMargin()

        execute(TrWord, TrRestJoined) { (action, userProvidedName) ->
            val alternativeName = userProvidedName.split(' ').joinToString(" ") { it.toLowerCase().capitalize() }
            send(
                when (action) {
                    "champion" -> LeagueChampionInfoSender(listOf(userProvidedName, alternativeName))
                    "item" -> LeagueItemInfoSender(listOf(userProvidedName, alternativeName))
                    "rune" -> LeagueRuneInfoSender(listOf(userProvidedName, alternativeName))
                    else -> {
                        failureDefault(this@command.name)
                        return@execute
                    }
                }
            )
        }
    }
}
