package dev.lunarcoffee.indigo.framework.core.services.paginators

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object PaginatorReactionListener : ListenerAdapter() {
    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (!PaginatorManager.isActivePaginator(event.messageIdLong))
            return

        val emoji = event.reaction.reactionEmote.takeIf { it.isEmoji }?.emoji ?: return
        val button = PaginatorButton.values().find { it.char == emoji } ?: return

        PaginatorManager.handleButtonClick(event.messageIdLong, button)
    }
}
