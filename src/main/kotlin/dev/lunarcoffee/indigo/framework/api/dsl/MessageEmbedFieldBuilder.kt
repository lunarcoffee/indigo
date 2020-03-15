package dev.lunarcoffee.indigo.framework.api.dsl

import net.dv8tion.jda.api.entities.MessageEmbed

class MessageEmbedFieldBuilderDsl {
    var name = ""
    var value = ""

    fun build(inline: Boolean) = MessageEmbed.Field(name, value, inline, true)
}
