package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import java.time.ZoneId

class TrTimeZone(private val ignoreCase: Boolean = false) : Transformer<ZoneId, CommandContext> {
    override val errorMessage = "A timezone could not be determined!"

    override fun transform(ctx: CommandContext, args: MutableList<String>): ZoneId? {
        val first = args.firstOrNull() ?: return null
        return ZoneId
            .of(ZoneId.getAvailableZoneIds().find { it.contains(first, ignoreCase) } ?: return null)
            .also { args.removeAt(0) }
    }
}
