package dev.lunarcoffee.indigo.bot.commands.moderation

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.await
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member

@CommandGroup("Moderation")
class ModerationCommands {
    fun kick() = command("kick") {
        description = """
            |`$name <user name/id/ping> [reason]`
            |Kicks a user from the current server.
            |This command will kick a specified `user` of the current server with an optional `reason` which will be
            |sent to them upon being kicked. To use this command, you and I must be able to kick members, and I must
            |have a high enough role to interact with the `user`.
            |&{Example usage:}
            |- `$name Jacob`\n
            |- `$name @Mailman didn't deliver the mail on time`
        """.trimMargin()

        execute(TrMember, TrRestJoined.optional { "(no reason)" }) { (member, reason) ->
            check(reason, "The reason can only be up to 800 characters long!") { length !in 1..800 }
            checkPermission("You must be able to kick members!", Permission.KICK_MEMBERS)
            checkPermission("I must be able to kick members!", Permission.KICK_MEMBERS, guild.selfMember)
            check(guild.selfMember, "I don't have a high enough role to do that!") { !canInteract(member) }

            if (checkFailed)
                return@execute

            sendActionResults(member, "kicked", "Kicker", reason)
            guild.kick(member).await()
        }
    }

    fun ban() = command("ban") {
        description = """
            |`$name <user name/id/ping> [reason]`
            |Bans a user from the current server.
            |This command will ban a specified `user` of the current server with an optional `reason` which will be
            |sent to them upon being banned. To use this command, you and I must be able to ban members, and I must
            |have a high enough role to interact with the `user`.
            |&{Example usage:}
            |- `$name AwesomeUser#1346`\n
            |- `$name engineer101 spam pinged admins at 3:00 am`
        """.trimMargin()

        execute(TrMember, TrRestJoined.optional { "(no reason)" }) { (member, reason) ->
            check(reason, "The reason can only be up to 800 characters long!") { length !in 1..800 }
            checkPermission("You must be able to ban members!", Permission.BAN_MEMBERS)
            checkPermission("I must be able to ban members!", Permission.BAN_MEMBERS, guild.selfMember)
            check(guild.selfMember, "I don't have a high enough role to do that!") { !canInteract(member) }

            if (checkFailed)
                return@execute

            sendActionResults(member, "banned", "Banner", reason)
            guild.ban(member, 0, reason).await()
        }
    }

    fun unban() = command("unban", "pardon") {
        description = """
            |`$name <user name/id>`
            |Unbans a user from the current server.
            |This command will unban a specified `user` who is banned in the current server. To use this command, you
            |and I must be able to ban members.
            |&{Example usage:}
            |- `$name NotThePerpetrator`\n
        """.trimMargin()

        execute(TrWord) { (nameOrId) ->
            checkPermission("You must be able to ban members!", Permission.BAN_MEMBERS)
            checkPermission("I must be able to ban members!", Permission.BAN_MEMBERS, guild.selfMember)

            val user = guild
                .retrieveBanList()
                .await()
                .find { nameOrId in listOf(it.user.name, it.user.name, it.user.asTag) }
                ?.user
            checkNull(user, "That user either is not banned or does not exist!") ?: return@execute

            guild.unban(user!!).await()
            success("`${user.asTag}` has been unbanned!")
        }
    }

    fun purge() = command("purge", "clear", "wipe") {
        description = """
            |`$name <limit> [user]`
            |Deletes a given amount of messages from the current channel.
            |This command will delete the last `limit` messages from the current channel, excluding the message 
            |invoking this command. If a `user` is specified, I will delete the last `limit` messages only from that
            |user. I will only delete up to 200 messages. To use this command, you can I must be able to manage 
            |messages.
            |&{Example usage:}
            |- `$name 100`\n
            |- `$name 200 server_raider_23`
        """.trimMargin()

        execute(TrInt, TrMember.optional()) { (limit, member) ->
            check(limit, "I can only purge up to 200 messages!") { this !in 1..200 }
            checkPermission("You must be able to manage messages!", Permission.MESSAGE_MANAGE)
            checkPermission("I must be able to manage messages!", Permission.MESSAGE_MANAGE, guild.selfMember)

            if (checkFailed)
                return@execute

            // Delete [limit] messages excluding the author's command invocation.
            val authorModifier = if (member == null || member.id == event.author.id) 1 else 0
            val messages = event
                .channel
                .iterableHistory
                .asSequence()
                .filter { it.author.id == member?.id ?: return@filter true }
                .take(limit + authorModifier)
                .toList()

            event.channel.purgeMessages(messages)
            success("Message purge complete!")
        }
    }

    fun slowmode() = command("slowmode", "cooldown") {
        description = """
            |`$name <slowmode cooldown>`
            |Sets the slowmode cooldown of the current channel.
            |This command sets the slowmode cooldown of the current channel to any arbitrary time between zero seconds
            |and six hours (inclusive), with second granularity. Because of this, this command provides more flexible
            |slowmode cooldown times than Discord's channel settings. To use this command, you and I must be able to
            |manage channels.
            |&{Example usage:}
            |- `$name 10s`
            |- `$name 3h 30m`
        """.trimMargin()

        execute(TrTime) { (cooldown) ->
            val cooldownSeconds = cooldown.totalSeconds
            check(cooldownSeconds, "The cooldown must be from zero seconds to six hours!") { this !in 0..21_600 }
            checkPermission("You must be able to manage channels!", Permission.MANAGE_CHANNEL)
            checkPermission("I must be able to manage channels!", Permission.MANAGE_CHANNEL, guild.selfMember)

            if (checkFailed)
                return@execute

            event.channel.manager.setSlowmode(cooldownSeconds.toInt()).await()
            val slowmodeString = if (cooldownSeconds > 0) "`$cooldown`" else "disabled"
            success("This channel's slowmode cooldown time is now $slowmodeString!")
        }
    }

    private suspend fun GuildCommandContext.sendActionResults(
        offender: Member,
        wordPast: String,
        wordNoun: String,
        reason: String
    ) {
        success("`${offender.user.asTag}` has been $wordPast!")
        offender.user.openPrivateChannel().await().send(
            embed {
                title = "${Emoji.HAMMER_AND_WRENCH}  You have been $wordPast!"
                description = """
                    |**Server**: ${guild.name}
                    |**$wordNoun**: ${event.author.asTag}
                    |**Reason**: $reason
                """.trimMargin()
            }
        )
    }
}
