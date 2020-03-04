package dev.lunarcoffee.indigo.framework.core.services.paginators

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

interface Paginator {
    val pages: List<Page>

    suspend fun start(ctx: CommandContext)
    suspend fun stop()

    suspend fun changePage(by: Int)
}
