package dev.lunarcoffee.indigo.bot.util

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object Database {
    private val client = KMongo.createClient().coroutine
    private val db = client.getDatabase("Indigo-0")

    val prefixStore = db.getCollection<PrefixPair>("PrefixStore")
}
