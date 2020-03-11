package dev.lunarcoffee.indigo.framework.core.std

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class TransformedTime(val totalSeconds: Long) {
    constructor(days: Int, hours: Int, minutes: Int, seconds: Int) :
            this(days * 86_400L + hours * 3_600 + minutes * 60 + seconds)

    fun asDuration() = Duration.of(totalSeconds, TimeUnit.SECONDS.toChronoUnit())
    fun asTimeFromNow(zone: ZoneId) = ZonedDateTime.now(zone).plusSeconds(totalSeconds)

    override fun toString(): String {
        return if (totalSeconds == 0L) {
            "0 seconds"
        } else {
            listOfNotNull(
                (totalSeconds / 86_400 % 365).ifOne("day"),
                (totalSeconds / 3_600 % 24).ifOne("hour"),
                (totalSeconds / 60 % 60).ifOne("minute"),
                (totalSeconds % 60).ifOne("second")
            ).joinToString(" ")
        }
    }

    // Pluralize based on value.
    private fun Long.ifOne(string: String) = "$this ${if (this == 1L) string else "${string}s"}".takeIf { this >= 1 }
}
