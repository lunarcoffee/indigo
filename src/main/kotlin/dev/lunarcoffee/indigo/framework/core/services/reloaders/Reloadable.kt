package dev.lunarcoffee.indigo.framework.core.services.reloaders

interface Reloadable {
    val timestamp: Long

    suspend fun schedule(data: Map<String, Any?>)
}
