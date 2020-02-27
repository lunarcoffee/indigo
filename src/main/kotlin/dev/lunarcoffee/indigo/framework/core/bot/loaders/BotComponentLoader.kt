package dev.lunarcoffee.indigo.framework.core.bot.loaders

import org.reflections.Reflections

abstract class BotComponentLoader<T> {
    abstract fun load(): T

    companion object {
        @JvmStatic
        protected val reflections = Reflections("dev.lunarcoffee.indigo") // TODO: dont hardcode source directory
    }
}
