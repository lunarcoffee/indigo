package dev.lunarcoffee.indigo.framework.core.services.reloaders

import com.google.gson.Gson
import dev.lunarcoffee.indigo.framework.core.bot.Bot
import java.io.File

class ReloadableManager {
    private val file by lazy { File(bot.config["reloadableFilePath"] ?: ".reloadables") }

    private val gson = Gson()
    private val cl = ClassLoader.getSystemClassLoader()!!

    lateinit var bot: Bot

    suspend fun reloadAll() {
        for (line in file.readLines()) {
            val (timestampStr, reloadableClassStr, dataStr) = line.split(' ', limit = 3)

            val timestamp = timestampStr.toLong()
            val reloadableClass = cl.loadClass(reloadableClassStr)
            val data = gson.fromJson(dataStr, HashMap<String, Any?>()::class.java)

            val reloadable = reloadableClass
                .constructors
                .find { it.parameterCount == 1 && it.parameters[0].type == Long::class.java }
                ?.newInstance(timestamp) as? Reloadable
                ?: continue

            reloadable.schedule(data)
        }
    }
}
