package dev.lunarcoffee.indigo.framework.core.services.paginators

import dev.lunarcoffee.indigo.framework.api.exts.clearReact
import dev.lunarcoffee.indigo.framework.api.exts.edit
import dev.lunarcoffee.indigo.framework.api.exts.react
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import net.dv8tion.jda.api.entities.Message

class Paginator(private val pages: List<Page>) {
    private var curPage = 0
    private val totalPages = pages.size

    private lateinit var message: Message
    private val messageId get() = message.idLong

    suspend fun start(ctx: CommandContext) {
        message = ctx.send(pages[curPage].asMessage(curPage + 1, totalPages))
        addButtons()
        PaginatorManager.register(messageId, this)
    }

    suspend fun stop() {
        message.clearReact()
        PaginatorManager.unregister(messageId)
    }

    suspend fun changePage(button: PaginatorButton) {
        val by = getShiftByButton(button) ?: return stop()

        curPage = (curPage + totalPages * 5 + by) % totalPages
        message.edit(pages[curPage].asMessage(curPage + 1, totalPages))
    }

    private suspend fun addButtons() {
        if (totalPages < 2)
            return

        for (button in PaginatorButton.values()) {
            if (button == PaginatorButton.JUMP_PREV || button == PaginatorButton.JUMP_NEXT && totalPages < 7)
                continue
            message.react(button.char)
        }
    }

    private fun getShiftByButton(button: PaginatorButton): Int? {
        return when (button) {
            PaginatorButton.FIRST -> -curPage
            PaginatorButton.JUMP_PREV -> -5
            PaginatorButton.PREV -> -1
            PaginatorButton.STOP -> null
            PaginatorButton.NEXT -> 1
            PaginatorButton.JUMP_NEXT -> 5
            PaginatorButton.LAST -> totalPages - curPage - 1
        }
    }
}
