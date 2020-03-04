package dev.lunarcoffee.indigo.framework.core.services.paginators

import net.dv8tion.jda.api.entities.Message

interface Page {
    fun asMessage(): Message
}
