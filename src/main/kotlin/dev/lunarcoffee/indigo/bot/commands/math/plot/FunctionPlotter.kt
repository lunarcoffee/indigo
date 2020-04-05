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

    fun plot(isPolar: Boolean = false): File? {
        val file = File(imageName)
        val image = BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().apply {
                // Fill background with white.
                fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE)

                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

                drawVisualAid(isPolar)
                for ((index, function) in functionStrings.withIndex())
                    drawFunction(isPolar, function, index) ?: return null
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

    private fun Graphics2D.drawFunction(isPolar: Boolean, function: String, index: Int) =
        if (isPolar) drawFunctionPolar(function, index) else drawFunctionCartesian(function, index)

    private fun Graphics2D.drawFunctionCartesian(function: String, index: Int): Unit? {
        for (increment in -PRECISION..PRECISION) {
            val x = increment.toDouble() / MULTIPLIER
            var evaluator = ExpressionCalculator(function.substituteVarX(x))
            val y = (evaluator.calculate() ?: return null) * MULTIPLIER

            val nextX = (increment + 1.0) / MULTIPLIER
            evaluator = ExpressionCalculator(function.substituteVarX(nextX))
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

    private fun Graphics2D.drawFunctionPolar(function: String, index: Int): Unit? {
        for (theta in 0..PRECISION * 2) {
            val angle = theta.toDouble() / PRECISION * PI
            var evaluator = ExpressionCalculator(function.substituteVarT(angle))
            val r = (evaluator.calculate() ?: return null)

            val x = r * cos(angle) * MULTIPLIER
            val y = r * sin(angle) * MULTIPLIER

            val nextAngle = (theta + 1.0) / PRECISION * PI
            evaluator = ExpressionCalculator(function.substituteVarT(nextAngle))
            val nextR = (evaluator.calculate() ?: return null)

            val nextX = nextR * cos(nextAngle) * MULTIPLIER
            val nextY = nextR * sin(nextAngle) * MULTIPLIER

            if (listOf(x, y, nextX, nextY).notSpecial()) {
                paint = COLORS[index]
                drawLine(
                    (ORIGIN + x).roundToInt(),
                    (IMAGE_SIZE - ORIGIN - y).roundToInt(),
                    (ORIGIN + nextX).roundToInt(),
                    (IMAGE_SIZE - ORIGIN - nextY).roundToInt()
                )
            }
        }
        return Unit
    }

    private fun String.substituteVarX(x: Double) = replace("x", "($x)")
    private fun String.substituteVarT(t: Double) = replace("t", "($t)")

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
