package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.TransformedTime

object TrTime : Transformer<TransformedTime> {
    // Accepted suffixes for time values in a time string.
    private val dayS = listOf("d", "day", "days")
    private val hourS = listOf("h", "hour", "hours")
    private val minuteS = listOf("m", "min", "mins", "minute", "minutes")
    private val secondS = listOf("s", "sec", "secs", "second", "seconds")

    private val timeStringRegex =
        """(\d{1,8})(${dayS.joinOr()}|${hourS.joinOr()}|${minuteS.joinOr()}|${secondS.joinOr()})""".toRegex()

    override fun transform(ctx: CommandContext, args: MutableList<String>): TransformedTime? {
        val timeStrings = args.takeWhile { it matches timeStringRegex }.ifEmpty { return null }
        args.removeAll(timeStrings)

        return timeStrings.run {
            TransformedTime(
                filterSuffixAndGet(dayS),
                filterSuffixAndGet(hourS),
                filterSuffixAndGet(minuteS),
                filterSuffixAndGet(secondS)
            )
        }
    }

    private fun List<String>.joinOr() = joinToString("|")

    // Remove any that doesn't end with the desired time guildsettings and sum the numbers from the remaining time strings
    // to get the total time value.
    private fun List<String>.filterSuffixAndGet(endsWith: List<String>) =
        filter { s -> endsWith.any { s.endsWith(it) } }
            .map { timeStringRegex.matchEntire(it)!!.groupValues[1] }
            .sumBy { it.toInt() }
}
