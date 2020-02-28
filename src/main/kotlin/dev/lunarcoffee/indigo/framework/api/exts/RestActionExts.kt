package dev.lunarcoffee.indigo.framework.api.exts

import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.requests.RestAction

suspend fun <T> RestAction<T>.await(): T = submit().await()
