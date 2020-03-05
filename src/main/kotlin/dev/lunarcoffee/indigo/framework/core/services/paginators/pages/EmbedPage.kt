package dev.lunarcoffee.indigo.framework.core.services.paginators.pages

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

class EmbedPage(private val embed: MessageEmbed) : Page {
    override val suppressEmbeds = false

    override fun asMessage(page: Int, totalPages: Int) = MessageBuilder()
        .setContent("[$page/$totalPages]")
        .setEmbed(embed)
        .build()
}
