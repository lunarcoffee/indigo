package dev.lunarcoffee.indigo.framework.core.bot

import net.dv8tion.jda.api.hooks.EventListener

interface ListenerBot : Bot {
    val listeners: List<EventListener>
}
