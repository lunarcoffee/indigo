package dev.lunarcoffee.indigo.framework.api.dsl

import net.dv8tion.jda.api.EmbedBuilder

class MessageEmbedBuilderDsl {
    private val embed = EmbedBuilder()

    var title: String? = null
        set(value) {
            field = value
            embed.setTitle(value)
        }

    var description = ""
        set(value) {
            field = value
            embed.setDescription(value)
        }

    var color = 0xD44F0D.also { embed.setColor(it) }
        set(value) {
            field = value
            embed.setColor(value)
        }

    var image: String? = null
        set(value) {
            field = value
            embed.setImage(value)
        }

    var thumbnail: String? = null
        set(value) {
            field = value
            embed.setThumbnail(value)
        }

    var footer: String? = null
        set(value) {
            field = value
            embed.setFooter(value)
        }

    fun field(name: String, value: String) = embed.addField(name, value, false).discard()
    fun inlineField(name: String, value: String) = embed.addField(name, value, true).discard()

    fun field(init: MessageEmbedFieldBuilderDsl.() -> Unit) =
        embed.addField(MessageEmbedFieldBuilderDsl().apply(init).build(false))

    fun inlineField(init: MessageEmbedFieldBuilderDsl.() -> Unit) =
        embed.addField(MessageEmbedFieldBuilderDsl().apply(init).build(true))

    fun author(name: String, url: String? = null, iconUrl: String? = null) =
        embed.setAuthor(name, url, iconUrl).discard()

    fun build() = embed.build()

    @Suppress("unused")
    private fun Any.discard() = Unit
}

fun embed(init: MessageEmbedBuilderDsl.() -> Unit) = MessageEmbedBuilderDsl().apply(init).build()
