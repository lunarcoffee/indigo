package dev.lunarcoffee.indigo.framework.core.std

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class TransformedTime(val days: Int, val hours: Int, val minutes: Int, val seconds: Int) {
    val totalSeconds = days * 86_400L + hours * 3_600 + minutes * 60 + seconds
    val totalMilliseconds = totalSeconds * 1_000

    fun asDuration() = Duration.of(totalSeconds, TimeUnit.SECONDS.toChronoUnit())
    fun asTimeFromNow(zone: ZoneId) = LocalDateTime.now(zone).plusSeconds(totalSeconds)

    override fun toString(): String {
        return if (totalSeconds == 0L) {
            "0 seconds"
        } else {
            listOfNotNull(
                days.ifOne("day"),
                hours.ifOne("hour"),
                minutes.ifOne("minute"),
                seconds.ifOne("second")
            ).joinToString(" ")
        }
    }

    // Pluralize based on value.
    private fun Int.ifOne(string: String) = "$this ${if (this != 1) string else "${string}s"}".takeIf { this >= 1 }
}
