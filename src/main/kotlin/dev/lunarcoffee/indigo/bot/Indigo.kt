package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.api.dsl.bot
import java.io.File

// FIXME: URGENT: regenerate token
fun main() {
    bot(true) {
        token = File("src/main/resources/token.txt").readText()
        prefix = { listOf("..") }
    }
}
