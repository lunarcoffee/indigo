package dev.lunarcoffee.indigo.bot.commands.math.plot

import dev.lunarcoffee.indigo.bot.commands.math.calc.ExpressionCalculator
import dev.lunarcoffee.indigo.framework.core.bot.Bot
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.roundToInt

class FunctionPlotter(val functionStrings: List<String>, val bot: Bot) {
    private val imageName = "${bot.config["resourceRoot"]}/temp/${functionStrings.hashCode()}.png"

    fun plot(): File? {
        val file = File(imageName)
        val image = BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().apply {
                // Fill background with white.
                fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE)

                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

                drawFunctions() ?: return null
                drawAxes()
            }
        }

        ImageIO.write(image, "png", file)
        return file
    }

    private fun Graphics2D.drawAxes() {
        paint = Color.BLACK

        drawLine(IMAGE_SIZE - PRECISION * INTERVAL * 2, ORIGIN, PRECISION * INTERVAL * 2, ORIGIN)
        drawLine(ORIGIN, IMAGE_SIZE - PRECISION * INTERVAL * 2, ORIGIN, PRECISION * INTERVAL * 2)

        for (n in 1 until 2 * PRECISION / MULTIPLIER) {
            if (n == PRECISION / MULTIPLIER)
                continue

            // Draw interval ticks.
            drawLine(n * MULTIPLIER, ORIGIN - 8, n * MULTIPLIER, ORIGIN + 8)
            drawLine(ORIGIN - 8, n * MULTIPLIER, ORIGIN + 8, n * MULTIPLIER)

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

    private fun Graphics2D.drawFunctions(): Unit? {
        for ((index, string) in functionStrings.withIndex()) {
            for (x in -PRECISION until PRECISION) {
                var evaluator = ExpressionCalculator(string.replace("x", "(${x.toDouble() / MULTIPLIER})"))
                val y = (evaluator.calculate() ?: return null) * INTERVAL * MULTIPLIER

                evaluator = ExpressionCalculator(string.replace("x", "(${(x + 1.0) / MULTIPLIER})"))
                val nextY = (evaluator.calculate() ?: return null) * INTERVAL * MULTIPLIER

                // Try to ignore discontinuities.
                if (
                    !y.isNaN() && !nextY.isNaN()
                    && y != Double.POSITIVE_INFINITY && nextY != Double.POSITIVE_INFINITY
                    && y != Double.NEGATIVE_INFINITY && nextY != Double.NEGATIVE_INFINITY
                    && abs(nextY - y) <= 5_000
                ) {
                    paint = COLORS[index]
                    drawLine(
                        ORIGIN + x * INTERVAL,
                        (IMAGE_SIZE - ORIGIN - y).roundToInt(),
                        ORIGIN + (x + 1) * INTERVAL,
                        (IMAGE_SIZE - ORIGIN - nextY).roundToInt()
                    )
                }
            }
        }
        return Unit
    }

    companion object {
        private const val IMAGE_SIZE = 800
        private const val ORIGIN = IMAGE_SIZE / 2
        private const val PRECISION = 400
        private const val INTERVAL = 1
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
