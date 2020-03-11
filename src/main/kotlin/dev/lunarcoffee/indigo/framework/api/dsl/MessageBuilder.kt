package dev.lunarcoffee.indigo.framework.api.dsl

import net.dv8tion.jda.api.MessageBuilder

class MessageBuilderDsl {
    private val message = MessageBuilder()

    var content: String? = null
        set(value) = message.setContent(value).discard()

    var nonce: String? = null
        set(value) = message.setNonce(nonce).discard()

    var tts = false
        set(value) = message.setTTS(value).discard()

    fun embed(init: MessageEmbedBuilderDsl.() -> Unit) = message.setEmbed(MessageEmbedBuilderDsl().apply(init).build())
    fun build() = message.build()

    @Suppress("unused")
    private fun Any.discard() = Unit
}

fun message(init: MessageBuilderDsl.() -> Unit) = MessageBuilderDsl().apply(init).build()
