package dev.lunarcoffee.indigo.framework.core.services.paginators

import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import net.dv8tion.jda.api.entities.Message

class EmbedPaginator(override val pages: List<Page>) : Paginator {
    private var curPage = 0
    private lateinit var message: Message

    override suspend fun start(ctx: CommandContext) {
        message = ctx.send(pages[curPage].asMessage())//todo: diff based on [pages.size]
    }

    override suspend fun stop() {

    }

    override suspend fun changePage(by: Int) {
        curPage = (curPage + pages.size * 5 + by) % pages.size

    }

    private fun editMessage() {

    }
}
