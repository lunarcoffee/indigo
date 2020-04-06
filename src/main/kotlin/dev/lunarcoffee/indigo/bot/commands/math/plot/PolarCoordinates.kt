package dev.lunarcoffee.indigo.bot.commands.math.plot

import kotlin.math.cos
import kotlin.math.sin

data class PolarCoordinates(val r: Double, val angle: Double) {
    fun toCartesian(scale: Int = 1) = CartesianCoordinates(r * cos(angle) * scale, r * sin(angle) * scale)
}
