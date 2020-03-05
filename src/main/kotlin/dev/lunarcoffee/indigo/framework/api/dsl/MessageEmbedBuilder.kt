package dev.lunarcoffee.indigo.framework.api.dsl

import net.dv8tion.jda.api.EmbedBuilder

class MessageEmbedBuilderDsl {
    private val embed = EmbedBuilder()

    var title: String? = null
        set(value) = embed.setTitle(value).discard()

    var description: String? = null
        set(value) = embed.setDescription(value).discard()

    var color = 0xFFFFFF
        set(value) = embed.setColor(value).discard()

    var image: String? = null
        set(value) = embed.setImage(value).discard()

    var thumbnail: String? = null
        set(value) = embed.setThumbnail(value).discard()

    var footer: String? = null
        set(value) = embed.setFooter(value).discard()

    fun author(name: String, url: String? = null, iconUrl: String? = null) =
        embed.setAuthor(name, url, iconUrl).discard()

    fun build() = embed.build()

    private fun Any.discard() = Unit
}

fun embed(init: MessageEmbedBuilderDsl.() -> Unit) = MessageEmbedBuilderDsl().apply(init).build()
