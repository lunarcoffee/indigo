package dev.lunarcoffee.indigo.bot.commands.`fun`

import dev.lunarcoffee.indigo.bot.commands.`fun`.roll.DiceRoll
import dev.lunarcoffee.indigo.bot.commands.`fun`.roll.TrDiceRoll
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.ifEmptyNone
import dev.lunarcoffee.indigo.framework.api.dsl.*
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import kotlin.math.abs
import kotlin.random.Random

@CommandGroup("Fun")
class FunCommands {
    private val eightBallResponses = listOf(
        "It is certain.", "It is decidedly so.", "Without a doubt.", "Yes - definitely.", "You may rely on it.",
        "As I see it, yes.", "Most likely.", "Outlook good.", "Yes.", "Signs point to yes.", "Reply hazy, try again.",
        "Ask again later.", "Better not tell you now.", "Cannot predict now.", "Concentrate and ask again.",
        "Don't count on it.", "My reply is no.", "My sources say no.", "Outlook not so good.", "Very doubtful."
    )

    fun flip() = command("flip", "coin") {
        description = """
            |`$name [times]`
            |Flips one or more coins.
            |This command will flip one coin, if `times` is not specified. If it is, I will flip that many coins, up to
            |10000 coins at most. Each coin should have a 50% chance of being either heads or tails.
            |&{Example usage}:
            |- `$name`\n
            |- `$name 3`
        """.trimMargin()

        execute(TrInt.optional { 1 }) { (times) ->
            check(times, "I can only flip up to 10000 coins!") { this !in 1..10_000 } ?: return@execute

            val flips = List(times) { Random.nextBoolean() }
            val coins = flips.map { if (it) "heads" else "tails" }

            val heads = flips.count { it }
            val embedTitle = "${Emoji.RADIO_BUTTON}  You flipped **" + if (times == 1)
                if (heads > 0) "heads**!" else "tails**!"
            else
                "$heads heads** and **${times - heads} tails**!"

            send(
                paginator {
                    for (page in coins.chunked(142)) {
                        embedPage {
                            title = embedTitle
                            description = page.toString()
                        }
                    }
                }
            )
        }
    }

    fun pick() = command("pick", "select", "choose") {
        description = """
            |`$name [count] <options...>`
            |Picks one or more options from a given list of options.
            |This command will pick from one to 200 (inclusive) options from a given list of up to 200 options. If you
            |provide a `count`, I will pick that many. If you don't, I will pick just one. If any of your options are
            |repeated, I may pick the option multiple times.
            |&{Options with spaces:}
            |Since the space character separates options, if an option contains a space character (i.e. in a name), you
            |can wrap it with single or double quotes (so `this text` would become `"this text"`). This also works with
            |other commands.
            |&{Example usage:}
            |- `$name strawberry chocolate vanilla`\n
            |- `$name "Andy S." "Carolyn A." "Trent T."`\n
            |- `$name 2 physics chemistry biology`
        """.trimMargin()

        execute(TrInt.optional { 1 }, TrRemaining) { (count, options) ->
            val mutableOptions = options.toMutableList()

            check(count, "I don't have enough options to pick from!") { this > mutableOptions.size } ?: return@execute
            check(options, "I can only pick from up to 200 options!") { size !in 1..200 } ?: return@execute

            val choices = List(count) { mutableOptions.random().also { mutableOptions -= it } }

            send(
                embed {
                    title = "${Emoji.THINKING}  I have decided:"
                    description = """
                        |**Picked**: $choices
                        |**Remaining**: ${mutableOptions.ifEmptyNone()}
                    """.trimMargin()
                }
            )
        }
    }

    fun eightBall() = command("8ball", "magicball", "magic8ball") {
        description = """
            |`$name <question>`
            |The Magic 8-Ball shall decide your fate.
            |With your `question`, I will take my Magic 8-Ball and tell you what it says.
            |&{Example usage:}
            |- `$name will i get into university?`\n
            |- `$name is it possible to take 2 days to buy cigarettes?`
        """.trimMargin()

        execute(TrRestJoined) { send("${Emoji.POOL_8_BALL}  **${eightBallResponses.random()}**") }
    }

    fun roll() = command("roll", "dice") {
        description = """
            |`$name [dice specifiers...]`
            |Rolls some dice according to some RPG style dice specifiers.
            |This command accepts many `dice specifiers` that look something like `2d8` or `d20+2` or `3d4-1`. If none
            |are given, I will roll a single `d6`. For every specifier, I can only roll the die at most 100 times, the 
            |die must have at most 1000 sides, and the modifier must be at most +/- 1000000.
            |&{Example usage:}
            |- `$name`\n
            |- `$name 2d8`\n
            |- `$name d20+2 3d4-1`
        """.trimMargin()

        execute(TrMany(TrDiceRoll).optional { listOf(DiceRoll(1, 6, 0)) }) { (dice) ->
            check(dice, "I can only roll up to 16 dice specifiers!") { size > 16 }
            check(dice, "I can only roll a die at most 100 times!") { any { it.times !in 1..100 } }
            check(dice, "I can only roll a die with 3 to 1000 sides (inclusive)!") { any { it.sides !in 3..1_000 } }
            check(dice, "I can only apply a modifier of at most +/- 1000000.") { any { abs(it.mod) > 1_000_000 } }

            if (checkFailed)
                return@execute

            val rolls = dice.map { die -> List(die.times) { Random.nextInt(1, die.sides + 1) } }
            val total = rolls.mapIndexed { index, roll -> roll.sum() + dice[index].mod }.sum()

            send(
                embed {
                    title = "${Emoji.GAME_DIE}  You rolled a total of **$total**!"

                    for ((index, roll) in rolls.withIndex()) {
                        val die = dice[index]
                        description += "**$die**: $roll "
                        if (die.mod != 0)
                            description += (if (die.mod > 0) "+" else "") + die.mod
                        description += '\n'
                    }
                }
            )
        }
    }
}
