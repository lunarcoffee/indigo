package dev.lunarcoffee.indigo.bot.commands.math.plot

import kotlin.math.*

data class CartesianCoordinates(val x: Double, val y: Double) {
    fun toPolar() = if (x == 0.0) PolarCoordinates(y, PI / 2) else PolarCoordinates(hypot(x, y), atan(y / x))
}
