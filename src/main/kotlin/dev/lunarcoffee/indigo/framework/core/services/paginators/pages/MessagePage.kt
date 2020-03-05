package dev.lunarcoffee.indigo.framework.core.services.paginators.pages

import net.dv8tion.jda.api.entities.Message

class MessagePage(private val message: Message, override val suppressEmbeds: Boolean) : Page {
    override fun asMessage(page: Int, totalPages: Int) = message
}
