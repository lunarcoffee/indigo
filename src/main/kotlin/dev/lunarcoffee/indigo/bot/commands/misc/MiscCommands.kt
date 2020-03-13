package dev.lunarcoffee.indigo.bot.commands.misc

import dev.lunarcoffee.indigo.bot.commands.config.status.SystemStatus
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
        description = """
            |`$name`
            |Shows a few calculations relating to the speed at which I can get to you.
            |This command shows the round-trip REST API latency, the time between the heartbeat send and the receiving
            |of the ACK response for the WebSocket gateway, the time it takes to allocate a kotlin.Int on the stack,
            |and the time it takes to allocate a java.lang.String with its no-args constructor on the heap.
        """.trimMargin()

        @Suppress("UNUSED_VARIABLE")
        execute {
            val restApiPing = jda.restPing.await()
            val stackLatency = measureNanoTime { val a = 0 }
            val heapLatency = measureNanoTime { val a = String() }

            send(
                embed {
                    title = "${Emoji.PING_PONG}  Pong!"
                    description = """
                        |**REST API**: $restApiPing ms
                        |**Heartbeat to ACK**: ${jda.gatewayPing} ms
                        |**Stack allocation**: $stackLatency ns
                        |**Heap allocation (string)**: $heapLatency ns
                    """.trimMargin()
                }
            )
        }
    }

    fun status() = command("status") {
        description = """
            |`$name`
            |Gives various stats on how I am running.
            |This command shows my memory usage, uptime, CPU architecture, logical CPU count (includes virtual CPUs
            |from technology like Intel Hyperthreading), and host operating system. It also shows what language I am
            |written in, the version of the JVM I am running on, and the number of alive threads.
        """.trimMargin()

        execute {
            SystemStatus().run {
                send(
                    embed {
                        title = "${Emoji.COMPUTER}  System status:"
                        description = """
                            |**Memory usage**: $usedMemory/$totalMemory MiB
                            |**Uptime**: $uptime
                            |**CPU architecture**: $cpuArchitecture
                            |**Logical CPUs**: $logicalProcessors
                            |**Operating system**: $osName
                            |**Language**: $language
                            |**JVM version**: $jvmVersion
                            |**Running/total threads**: $runningThreads/$totalThreads
                        """.trimMargin()
                    }
                )
            }
        }
    }
}
