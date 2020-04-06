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
                // Fill background with white.
                fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE)

//                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
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
                drawLine(
                    ORIGIN + increment,
                    (IMAGE_SIZE - ORIGIN - y).roundToInt(),
                    ORIGIN + (increment + 1),
                    (IMAGE_SIZE - ORIGIN - nextY).roundToInt()
                )
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
            val (x, y) = PolarCoordinates(evaluator.calculate() ?: return null, angle).toCartesian(MULTIPLIER)

            val nextAngle = (increment + 1.0) / PRECISION * PI
            evaluator.setVariable("t", nextAngle)
            val (nextX, nextY) = PolarCoordinates(evaluator.calculate() ?: return null, angle).toCartesian(MULTIPLIER)

            if (listOf(x, y, nextX, nextY).notSpecial()) {
                val (p1, p2) = getCoordinatesInBounds(x, y, nextX, nextY)
                paint = COLORS[index]
                drawLine(p1.x.roundToInt(), p1.y.roundToInt(), p2.x.roundToInt(), p2.y.roundToInt())
            }
        }
        return Unit
    }

    // TODO: write description as to why this is needed
    private fun getCoordinatesInBounds(x1: Double, x2: Double, y1: Double, y2: Double): CartesianLine {
        // Calculate scope, handling cases where the line is vertical or horizontal.
        val slope = (y2 - y1) / (x2 - x1)
        if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY)
            return CartesianLine(x1, 0.0, x1, IMAGE_SIZE.toDouble())
        if (slope == 0.0)
            return CartesianLine(0.0, y1, IMAGE_SIZE.toDouble(), y1)

        // The possible intersections of this line with the four edges of the image.
        val left = y1 - slope * x1
        val top = -left / slope
        val right = slope * IMAGE_SIZE + left
        val bottom = (IMAGE_SIZE - left) / slope

        val intersecting = listOf(left, top, right, bottom).filter { it.inBounds() }
        return when (intersecting.size) {
            0 -> CartesianLine(x1, x2, y1, y2)
            1 -> {
                val x = selectInBounds(x1, x2)
                val y = selectInBounds(y1, y2)
                val (boundedX, boundedY) = when (intersecting[0]) {
                    left -> Pair(0.0, left)
                    top -> Pair(top, 0.0)
                    right -> Pair(IMAGE_SIZE.toDouble(), right)
                    else -> Pair(bottom, IMAGE_SIZE.toDouble())
                }
                CartesianLine(boundedX, boundedY, x, y)
            }
            else -> when (intersecting) {
                listOf(left, top) -> CartesianLine(0, left, top, 0)
                listOf(top, right) -> CartesianLine(top, 0, IMAGE_SIZE, right)
                listOf(right, bottom) -> CartesianLine(IMAGE_SIZE, right, bottom, IMAGE_SIZE)
                listOf(bottom, left) -> CartesianLine(bottom, IMAGE_SIZE, 0, left)
                listOf(left, right) -> CartesianLine(0, left, IMAGE_SIZE, right)
                else -> CartesianLine(top, 0, bottom, IMAGE_SIZE)
            }
        }
    }

    private fun Double.inBounds() = this in 0.0..IMAGE_SIZE.toDouble()
    private fun selectInBounds(first: Double, second: Double) = if (first.inBounds()) first else second

    private fun List<Double>.notSpecial() =
        none { it == Double.POSITIVE_INFINITY || it == Double.NEGATIVE_INFINITY || it.isNaN() }

    companion object {
        private const val IMAGE_SIZE = 800
        private const val ORIGIN = IMAGE_SIZE / 2
        private const val PRECISION = 400
        private const val MULTIPLIER = 40

        private val COLORS = arrayOf(
            Color.RED,
            Color.BLUE,
            Color.GREEN.darker(),
            Color.CYAN.darker(),
            Color.MAGENTA
        )
    }
}
