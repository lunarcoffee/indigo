package dev.lunarcoffee.indigo.framework.core.std

class ClockTime(val hour: Int, val minute: Int, val isPm: Boolean) {
    fun toStringNormal() = toString().trimStart('0')

    override fun toString() = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} " +
            if (isPm) "PM" else "AM"
}
