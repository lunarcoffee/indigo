package dev.lunarcoffee.indigo.bot.commands.service.xkcd

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import dev.lunarcoffee.indigo.bot.util.consts.gson

class XkcdComicRequester(private val number: Int?) {
    suspend fun get(): XkcdComic? {
        if (number == 404)
            return XkcdComic.COMIC_404

        val url = API_ROOT + (if (number == null) "" else "/$number") + "/info.0.json"
        val (_, _, json) = runCatching { Fuel.get(url).awaitStringResponse() }.getOrNull() ?: return null

        return gson.fromJson(json, XkcdComic::class.java)
    }

    companion object {
        private const val API_ROOT = "http://xkcd.com"
    }
}
