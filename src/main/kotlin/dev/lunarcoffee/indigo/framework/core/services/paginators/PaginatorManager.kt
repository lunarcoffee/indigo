package dev.lunarcoffee.indigo.framework.core.services.paginators

import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

object PaginatorManager {
    private val active = ConcurrentHashMap<Long, Pair<Paginator, TimerTask>>()

    private val closeTimer = Timer()
    private const val CLOSE_TIMEOUT = 120_000L

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun register(messageId: Long, paginator: Paginator) {
        active[messageId] = Pair(paginator, scheduleCloseTask(messageId))
    }

    fun unregister(messageId: Long) {
        active -= messageId
    }

    fun handleButtonClick(messageId: Long, button: PaginatorButton) {
        val (paginator, timerTask) = active[messageId] ?: return unregister(messageId)
        timerTask.cancel()

        coroutineScope.launch { paginator.changePage(button) }
        active[messageId] = Pair(paginator, scheduleCloseTask(messageId))
    }

    fun isPaginatorOwner(userId: String, messageId: Long) =
        active[messageId] != null && active[messageId]?.first?.ownerId == userId

    private fun scheduleCloseTask(messageId: Long) = closeTimer
        .schedule(CLOSE_TIMEOUT) {
            if (active[messageId] != null)
                coroutineScope.launch { active[messageId]?.first?.stop() }
        }
}
