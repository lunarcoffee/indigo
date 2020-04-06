package dev.lunarcoffee.indigo.bot.commands.math.plot

class CartesianLine(x1: Number, y1: Number, x2: Number, y2: Number) {
    val p1 = CartesianCoordinates(x1.toDouble(), y1.toDouble())
    val p2 = CartesianCoordinates(x2.toDouble(), y2.toDouble())
    val slope = (p2.y - p1.y) / (p2.x - p1.x)

    operator fun component1() = p1
    operator fun component2() = p2
}
