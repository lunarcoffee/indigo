package dev.lunarcoffee.indigo.bot.commands.`fun`.roll

class DiceRoll(val times: Int, val sides: Int, val mod: Int) {
    override fun toString(): String {
        var result = "${times}d$sides"

        if (times == 1)
            result = result.drop(1)
        if (mod > 0)
            result += '+'

        return result + if (mod != 0) mod.toString() else ""
    }
}
