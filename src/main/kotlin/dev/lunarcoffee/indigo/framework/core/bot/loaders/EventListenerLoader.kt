package dev.lunarcoffee.indigo.framework.core.bot.loaders

import dev.lunarcoffee.indigo.framework.core.commands.ListenerGroup
import net.dv8tion.jda.api.hooks.EventListener

class EventListenerLoader : BotComponentLoader<List<EventListener>>() {
    override fun load(): List<EventListener> {
        return reflections
            .getTypesAnnotatedWith(ListenerGroup::class.java)
            .filter { EventListener::class.java in it.interfaces } // TODO:
            .mapNotNull { it.constructors.getOrNull(0)?.newInstance() as EventListener }
    }
}
