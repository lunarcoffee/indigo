package dev.lunarcoffee.indigo.framework.core.services.paginators.pages

import net.dv8tion.jda.api.entities.Message

class MessagePage(override val suppressEmbeds: Boolean, private val message: (Int, Int) -> Message) : Page {
    override fun asMessage(page: Int, totalPages: Int) = message(page, totalPages)
}
