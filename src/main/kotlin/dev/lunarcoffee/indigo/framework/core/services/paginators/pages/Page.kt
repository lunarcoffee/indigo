package dev.lunarcoffee.indigo.framework.core.services.paginators.pages

import net.dv8tion.jda.api.entities.Message

interface Page {
    val suppressEmbeds: Boolean

    fun asMessage(page: Int, totalPages: Int): Message
}
