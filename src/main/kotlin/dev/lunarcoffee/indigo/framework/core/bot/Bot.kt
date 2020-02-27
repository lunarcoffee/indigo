package dev.lunarcoffee.indigo.framework.core.bot

import dev.lunarcoffee.indigo.framework.core.std.HasJDA

interface Bot : HasJDA {
    suspend fun start()
}
