package dev.lunarcoffee.indigo.framework.core.services.paginators

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

class EmbedPage(private val embed: MessageEmbed) : Page {
    override fun asMessage(page: Int, totalPages: Int) = MessageBuilder()
        .setContent("[$page/$totalPages]")
        .append(embed)
        .build()
}
