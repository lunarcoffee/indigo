package dev.lunarcoffee.indigo.bot.commands.`fun`.roll

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.commands.transformers.Transformer

object TrDiceRoll : Transformer<DiceRoll> {
    private val DICE_ROLL_REGEX = """(\d*)d(\d+)([+-]\d+)?""".toRegex()

    override val errorMessage = "A dice specifier was formatted incorrectly!"

    override fun transform(ctx: CommandContext, args: MutableList<String>): DiceRoll? {
        val first = args.firstOrNull() ?: return null
        val match = DICE_ROLL_REGEX.matchEntire(first) ?: return null

        val (timesS, sidesS, modS) = match.destructured

        val times = timesS.toIntOrNull() ?: 1
        val sides = sidesS.toIntOrNull() ?: return null
        val mod = (if (modS.startsWith('+')) modS.drop(1) else modS.ifEmpty { "0" }).toIntOrNull() ?: return null

        args.removeAt(0)
        return DiceRoll(times, sides, mod)
    }
}
