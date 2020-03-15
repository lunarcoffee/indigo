package dev.lunarcoffee.indigo.bot.commands.misc.status

import dev.lunarcoffee.indigo.framework.core.std.TransformedTime
import java.lang.management.ManagementFactory

class SystemStatus {
    val totalMemory = Runtime.getRuntime().totalMemory() / 1_000_000
    val usedMemory = totalMemory - Runtime.getRuntime().freeMemory() / 1_000_000

    val uptime = TransformedTime(ManagementFactory.getRuntimeMXBean().uptime / 1_000)

    val cpuArchitecture = retrieveCpuArchitecture()
    val logicalProcessors = Runtime.getRuntime().availableProcessors()

    val osName = System.getProperty("os.name") ?: "(unknown)"
    val language = "Kotlin ${KotlinVersion.CURRENT}"
    val jvmVersion = System.getProperty("java.version") ?: "(unknown)"

    private val threads = Thread.getAllStackTraces().keys
    var runningThreads = threads.filter { it.state == Thread.State.RUNNABLE }.size
    val totalThreads = threads.size

    private fun retrieveCpuArchitecture(): String {
        return if ("Windows" in System.getProperty("os.name")) {
            val cpuArch = System.getenv("PROCESSOR_ARCHITECTURE")
            val wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432")

            if (cpuArch != null && "64" in cpuArch || wow64Arch != null && "64" in wow64Arch) "64-bit" else "32-bit"
        } else {
            when (System.getProperty("os.arch")) {
                "amd64" -> "64-bit"
                "ia64" -> "Itanium 64-bit"
                "x86" -> "32-bit"
                else -> "(unknown)"
            }
        }
    }
}
