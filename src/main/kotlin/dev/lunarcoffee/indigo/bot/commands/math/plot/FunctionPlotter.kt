package dev.lunarcoffee.indigo.bot.commands.math.plot

import dev.lunarcoffee.indigo.bot.commands.math.calc.ExpressionCalculator
import dev.lunarcoffee.indigo.framework.core.bot.Bot
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.*

class FunctionPlotter(private val functionStrings: List<String>, val bot: Bot) {
    private val imageName = "${bot.config["resourceRoot"]}/temp/${functionStrings.hashCode()}.png"

    fun plot(isPolar: Boolean, domain: Double): File? {
        val file = File(imageName)
        val image = BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().apply {
                fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE)

                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

                drawVisualAid(isPolar)
                for ((index, function) in functionStrings.withIndex())
                    drawFunction(isPolar, ExpressionCalculator(function), index, domain) ?: return null
                drawAxes()
            }
        }

        ImageIO.write(image, "png", file)
        return file
    }

    private fun Graphics2D.drawAxes() {
        paint = Color.BLACK

        // Draw axes.
        drawLine(IMAGE_SIZE - PRECISION * 2, ORIGIN, PRECISION * 2, ORIGIN)
        drawLine(ORIGIN, IMAGE_SIZE - PRECISION * 2, ORIGIN, PRECISION * 2)

        for (n in 1 until 2 * PRECISION / MULTIPLIER) {
            if (n == PRECISION / MULTIPLIER)
                continue

            val xLabelOffset = n * MULTIPLIER - when (n - PRECISION / MULTIPLIER) {
                in 1..9 -> 4
                in 10..99, in -9..-1 -> 8
                else -> 12
            }

            // Draw interval labels.
            drawString((n - PRECISION / MULTIPLIER).toString(), xLabelOffset, ORIGIN - 16)
            drawString((PRECISION / MULTIPLIER - n).toString(), ORIGIN + 16, n * MULTIPLIER + 4)

            // Draw interval ticks.
            drawLine(n * MULTIPLIER, ORIGIN - 8, n * MULTIPLIER, ORIGIN + 8)
            drawLine(ORIGIN - 8, n * MULTIPLIER, ORIGIN + 8, n * MULTIPLIER)
        }
    }

    private fun Graphics2D.drawVisualAid(isPolar: Boolean) {
        paint = Color.LIGHT_GRAY

        if (isPolar) {
            // Draw circles.
            for (n in 1 until PRECISION / MULTIPLIER * ceil(sqrt(2.0)).toInt())
                drawOval(ORIGIN - n * MULTIPLIER, ORIGIN - n * MULTIPLIER, 2 * n * MULTIPLIER, 2 * n * MULTIPLIER)

            // Draw diagonal lines pi/6 radians apart.
            for (angle in (1..12).map { it * PI / 12 }) {
                if (angle != 0.0) {
                    val x = (15 * cos(angle) * MULTIPLIER).roundToInt()
                    val y = (15 * sin(angle) * MULTIPLIER).roundToInt()
                    drawLine(x + ORIGIN, y + ORIGIN, ORIGIN - x, ORIGIN - y)
                }
            }
        } else {
            // Draw grid lines.
            for (n in 1 until 2 * PRECISION / MULTIPLIER) {
                drawLine(n * MULTIPLIER, 0, n * MULTIPLIER, IMAGE_SIZE)
                drawLine(0, n * MULTIPLIER, IMAGE_SIZE, n * MULTIPLIER)
            }
        }
    }

    private fun Graphics2D.drawFunction(polar: Boolean, evaluator: ExpressionCalculator, index: Int, domain: Double) =
        if (polar) drawFunctionPolar(evaluator, index, domain) else drawFunctionCartesian(evaluator, index)

    private fun Graphics2D.drawFunctionCartesian(evaluator: ExpressionCalculator, index: Int): Unit? {
        for (increment in -PRECISION..PRECISION) {
            val x = increment.toDouble() / MULTIPLIER
            evaluator.setVariable("x", x)
            val y = (evaluator.calculate() ?: return null) * MULTIPLIER

            val nextX = (increment + 1.0) / MULTIPLIER
            evaluator.setVariable("x", nextX)
            val nextY = (evaluator.calculate() ?: return null) * MULTIPLIER

            if (listOf(y, nextY).notSpecial() && abs(nextY - y) <= 5_000) {
                paint = COLORS[index]

                val (p1, p2) = getLineInBounds(
                    x * MULTIPLIER + ORIGIN,
                    ORIGIN - y,
                    nextX * MULTIPLIER + ORIGIN,
                    ORIGIN - nextY
                ) ?: continue

                drawLine(p1.x.roundToInt(), p1.y.roundToInt(), p2.x.roundToInt(), p2.y.roundToInt())
            }
        }
        return Unit
    }

    private fun Graphics2D.drawFunctionPolar(evaluator: ExpressionCalculator, index: Int, domain: Double): Unit? {
        val domainMultiplier = domain / 2 / PI
        val upperBound = (PRECISION * 2 * domainMultiplier).roundToInt()

        for (increment in 0..upperBound) {
            val angle = increment.toDouble() / PRECISION * PI
            evaluator.setVariable("t", angle)
            val r = evaluator.calculate() ?: return null
            val (x, y) = PolarCoordinates(r, angle).toCartesian()

            val nextAngle = (increment + 1.0) / PRECISION * PI
            evaluator.setVariable("t", nextAngle)
            val nextR = evaluator.calculate() ?: return null
            val (nextX, nextY) = PolarCoordinates(nextR, nextAngle).toCartesian()

            if (listOf(x, y, nextX, nextY).notSpecial() && abs(nextR - r) <= 5_000) {
                val (p1, p2) = getLineInBounds(
                    x * MULTIPLIER + ORIGIN,
                    ORIGIN - y * MULTIPLIER,
                    nextX * MULTIPLIER + ORIGIN,
                    ORIGIN - nextY * MULTIPLIER
                ) ?: continue

                paint = COLORS[index]
                drawLine(p1.x.roundToInt(), p1.y.roundToInt(), p2.x.roundToInt(), p2.y.roundToInt())
            }
        }
        return Unit
    }

    // This method corrects a given line segment from the points (x1, y1) to (x2, y2) to be within a certain distance
    // from the visible edge of the image before being drawn. This is necessary because of the apparent behavior of
    // [Graphics.drawLine] when antialiasing is enabled, where even parts of the line outside of the image buffer will
    // be considered in logic surrounding the drawing. This causes massive performance impacts when plots, particularly
    // polar plots, are drawn with sufficient amounts of long lines due to asymptotes and similar phenomena, so this
    // method exists to combat those losses.
    private fun getLineInBounds(x1: Double, y1: Double, x2: Double, y2: Double): CartesianLine? {
        // Lower and upper locations in pixels on the image to consider as the edge. These values purposely form a
        // bounding box larger than actual image to prevent visual artifacts near the visible edge of the image.
        val lowerEdge = -150.0
        val upperEdge = IMAGE_SIZE + 150.0

        fun Double.inBounds() = this in lowerEdge..upperEdge
        fun selectInBounds(first: Double, second: Double) = if (first.inBounds()) first else second

        // Don't bother drawing a line off the screen.
        if (!x1.inBounds() && !x2.inBounds() || !y1.inBounds() && !y2.inBounds())
            return null

        val slope = (y2 - y1) / (x2 - x1)
        val yInt = y1 - slope * x1

        // The possible intersections of this line with the four edges of the image.
        val left = slope * lowerEdge + yInt
        val top = (lowerEdge - yInt) / slope
        val right = slope * upperEdge + yInt
        val bottom = (upperEdge - yInt) / slope

        // List of all intersecting points.
        val xRange = min(x1, x2)..max(x1, x2)
        val yRange = min(y1, y2)..max(y1, y2)
        val intersecting = listOf(left, top, right, bottom)
            .zip(listOf(lowerEdge, lowerEdge, upperEdge, upperEdge))
            .filter { (point, other) ->
                // Ensure that the points are within the box that bounds the original given line segment.
                val index = point == left || point == right
                point in (if (index) yRange else xRange) && other in (if (index) xRange else yRange)
            }
            .map { it.first }

        return when (intersecting.size) {
            // Return the line from the intersection to the point which is inside the image.
            1 -> {
                val x = selectInBounds(x1, x2)
                val y = selectInBounds(y1, y2)
                val (boundedX, boundedY) = when (intersecting[0]) {
                    left -> Pair(lowerEdge, left)
                    top -> Pair(top, lowerEdge)
                    right -> Pair(upperEdge, right)
                    else -> Pair(bottom, upperEdge)
                }
                CartesianLine(boundedX, boundedY, x, y)
            }
            // Return the line connecting the two intersections on the edge.
            2 -> when (intersecting) {
                listOf(left, top) -> CartesianLine(lowerEdge, left, top, lowerEdge)
                listOf(top, right) -> CartesianLine(top, lowerEdge, upperEdge, right)
                listOf(right, bottom) -> CartesianLine(upperEdge, right, bottom, upperEdge)
                listOf(bottom, left) -> CartesianLine(bottom, upperEdge, lowerEdge, left)
                listOf(left, right) -> CartesianLine(lowerEdge, left, upperEdge, right)
                else -> CartesianLine(top, lowerEdge, bottom, upperEdge)
            }
            // Just return the same line. Corner intersections may count as three or even four intersections, but not
            // enough will do this for a meaningful performance impact.
            else -> CartesianLine(x1, y1, x2, y2)
        }
    }

    private fun List<Double>.notSpecial() =
        none { it == Double.POSITIVE_INFINITY || it == Double.NEGATIVE_INFINITY || it.isNaN() }

    companion object {
        private const val IMAGE_SIZE = 800
        private const val ORIGIN = IMAGE_SIZE / 2
        private const val RANGE = 10
        private const val MULTIPLIER = 40
        private const val PRECISION = RANGE * MULTIPLIER

        private val COLORS = arrayOf(
            Color.RED,
            Color.BLUE,
            Color.GREEN.darker(),
            Color.CYAN.darker(),
            Color.MAGENTA
        )
    }
}
