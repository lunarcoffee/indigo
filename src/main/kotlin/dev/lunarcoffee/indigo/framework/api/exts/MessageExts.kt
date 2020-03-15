package dev.lunarcoffee.indigo.framework.api.exts

import net.dv8tion.jda.api.entities.*

suspend fun Message.edit(text: CharSequence): Message = editMessage(text).await()
suspend fun Message.edit(message: Message): Message = editMessage(message).await()
suspend fun Message.edit(embed: MessageEmbed): Message = editMessage(embed).await()

suspend fun Message.remove() {
    delete().await()
}

suspend fun Message.react(unicode: String) {
    addReaction(unicode).await()
}

suspend fun Message.react(emote: Emote) {
    addReaction(emote).await()
}
