package dev.lunarcoffee.indigo.bot.commands.misc

import dev.lunarcoffee.indigo.bot.commands.misc.status.SystemStatus
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.await
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrWord
import kotlin.system.measureNanoTime

@CommandGroup("Misc")
class MiscCommands {
    fun ping() = command("ping", "pong", "peng") {
        description = """
            |`$name`
            |Shows a few calculations relating to the speed at which I can get to you.
            |This command shows the round-trip REST API latency, the time between the heartbeat send and the receiving
            |of the ACK response for the WebSocket gateway, and the time it takes to allocate a java.lang.String with 
            |its no-args constructor on the heap.
        """.trimMargin()

        @Suppress("UNUSED_VARIABLE")
        execute {
            val restApiPing = jda.restPing.await()
            val heapLatency = measureNanoTime { val a = String() }

            send(
                embed {
                    title = "${Emoji.PING_PONG}  Pong!"
                    description = """
                        |**REST API**: $restApiPing ms
                        |**Heartbeat to ACK**: ${jda.gatewayPing} ms
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

    fun invite() = command("invite", "inviteme") {
        description = """
            |`$name ["noperms"]`
            |Gives you a link which you can use to invite me.
            |This command will give you a link which can be used to invite me to a server. If `noperms` is specified, I
            |will remove all permissions from the link (i.e. you don't have to let me be able to kick/ban/etc. members
            |if you do not plan on using that functionality), though it is possible to manually uncheck them as well.
            |&{Example usage:}
            |- `$name`\n
            |- `$name noperms`
        """.trimMargin()

        execute(TrWord.optional()) { (noPerms) ->
            val id = bot.jda.selfUser.id
            val perms = if (noPerms == "noperms") "0" else "1879436358"
            val url = "https://discordapp.com/api/oauth2/authorize?client_id=$id&permissions=$perms&scope=bot"

            success("You can invite me with <$url>!")
        }
    }
}
