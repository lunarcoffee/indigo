@file:Suppress("UNUSED_VARIABLE")

package dev.lunarcoffee.indigo.bot.commands.misc

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.await
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import kotlin.system.measureNanoTime

@CommandGroup("Misc")
class MiscCommands {
    fun ping() = command("ping", "pong", "peng") {
        description = "pings bot"

        execute {
            val restApiPing = jda.restPing.await()
            val stackLatency = measureNanoTime { val a = 0 }
            val heapLatency = measureNanoTime { val a = String() }

            send(
                embed {
                    title = "${Emoji.PING_PONG}  Pong!"
                    description = """
                        |REST API: $restApiPing ms
                        |Heartbeat to ACK: ${jda.gatewayPing} ms
                        |Stack allocation: $stackLatency ns
                        |Heap allocation: $heapLatency ns
                    """.trimMargin()
                }
            )
        }
    }
}
