package dev.lunarcoffee.indigo.bot.commands.math.plot

import kotlin.math.cos
import kotlin.math.sin

data class PolarCoordinates(val r: Double, val angle: Double) {
    fun toCartesian() = CartesianCoordinates(r * cos(angle), r * sin(angle))
}
