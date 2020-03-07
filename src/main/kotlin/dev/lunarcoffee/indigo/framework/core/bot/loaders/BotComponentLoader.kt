package dev.lunarcoffee.indigo.framework.core.bot.loaders

import org.reflections.Reflections

abstract class BotComponentLoader<T>(sourceRoot: String) {
    protected val reflections = Reflections(sourceRoot)

    abstract fun load(): T
}
