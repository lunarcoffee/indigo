package dev.lunarcoffee.indigo.framework.api.dsl

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.services.paginators.Paginator
import dev.lunarcoffee.indigo.framework.core.services.paginators.pages.EmbedPage
import dev.lunarcoffee.indigo.framework.core.services.paginators.pages.MessagePage
import dev.lunarcoffee.indigo.framework.core.services.paginators.pages.Page
import dev.lunarcoffee.indigo.framework.core.services.paginators.pages.TextPage
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class PaginatorBuilderDsl(private val ownerId: String) {
    private val pages = mutableListOf<Page>()

    fun embedPage(embedBuilder: MessageEmbedBuilderDsl.() -> Unit) = pages.add(EmbedPage(embed(embedBuilder)))
    fun textPage(text: String) = pages.add(TextPage(text))
    fun messagePage(suppressEmbeds: Boolean, message: Message) = pages.add(MessagePage(message, suppressEmbeds))

    fun build() = Paginator(pages, ownerId)
}

fun CommandContext.paginator(init: PaginatorBuilderDsl.() -> Unit) =
    PaginatorBuilderDsl((event as GuildMessageReceivedEvent).author.id).apply(init).build()
