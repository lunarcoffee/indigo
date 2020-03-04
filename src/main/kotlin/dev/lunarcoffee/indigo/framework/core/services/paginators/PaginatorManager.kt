package dev.lunarcoffee.indigo.framework.core.services.paginators

object PaginatorManager {
    private val active = mutableMapOf<Long, Paginator>()

    fun unregister(messageId: Long) {
        active -= messageId
    }
}
