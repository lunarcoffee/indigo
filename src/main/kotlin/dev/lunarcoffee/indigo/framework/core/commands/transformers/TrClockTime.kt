package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ClockTime

object TrClockTime : Transformer<ClockTime> {
    override fun transform(ctx: CommandContext, args: MutableList<String>): ClockTime? {
        val first = args.firstOrNull() ?: return null

        val amPm = first.takeLast(2)
        val isPm = amPm.equals("pm", true)
        if (!amPm.equals("am", true) && !isPm)
            return null

        val time = first.dropLast(2).split(':')
        if (time.size != 2 || time.any { it.toIntOrNull() == null })
            return null

        val (hour, minute) = time.map { it.toInt() }
        if (hour !in 1..12 || minute !in 0..59)
            return null

        args.removeAt(0)
        return ClockTime(hour, minute, isPm)
    }
}
