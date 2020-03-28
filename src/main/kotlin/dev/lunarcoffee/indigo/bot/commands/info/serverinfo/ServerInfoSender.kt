package dev.lunarcoffee.indigo.bot.commands.info.serverinfo

import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.paginator
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Guild

class ServerInfoSender(private val guild: Guild) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            ctx.paginator {
                embedPage {
                    title = "${Emoji.MAG}  Info on server **${guild.name.sanitize()}**:"
                    description = """
                        |**Server ID**: ${guild.id}
                        |**Total members**: ${guild.memberCount}
                        |**Total channels**: ${guild.channels.size}
                        |**Text channels**: ${guild.textChannels.size}
                        |**Voice channels**: ${guild.voiceChannels.size}
                        |**Total categories**: ${guild.categories.size}
                        |**NSFW filter**: ${guild.explicitContentLevel.description}
                        |**Region**: ${guild.region.getName()}
                        |**Time created**: ${guild.timeCreated.formatDefault()}
                        |**Features**: ${guild.features.map { "`$it`" }.ifEmptyNone()}
                    """.trimMargin()
                    thumbnail = guild.iconUrl
                }
                embedPage {
                    title = "${Emoji.MAG}  Info on server **${guild.name.sanitize()}**:"
                    description = """
                        |**Boost tier**: ${guild.boostTier.key.ifZeroNone()}
                        |**Boosters**: ${guild.boosters.size}
                        |**Boost count**: ${guild.boostCount}
                        |**Description**: ${guild.description.ifNullNone()}
                        |**Owner**: ${guild.owner?.asMention.ifNullNone()}
                        |**Verification**: ${guild.verificationLevel.name.toLowerCase().replace('_', ' ')}
                        |**Banner art**: ${guild.bannerUrl.ifNullNoneElseLink()}
                        |**Banner ID**: ${guild.bannerId.ifNullNone()}
                        |**Vanity code**: ${guild.vanityCode.ifNullNone()}
                        |**Splash art**: ${guild.splashUrl.ifNullNoneElseLink()}
                    """.trimMargin()
                    thumbnail = guild.iconUrl
                }
                embedPage {
                    title = "${Emoji.MAG}  Info on server **${guild.name.sanitize()}**:"
                    description = """
                        |**Splash ID**: ${guild.splashId.ifNullNone()}
                        |**MFA required**: ${(guild.requiredMFALevel.key == 1).toYesNo()}
                        |**Roles**: ${guild.roles.map { it.asMention }.ifEmptyNone()}
                        |**Public permissions**: ${guild.publicRole.permissions.map { "`${it}`" }.ifEmptyNone()}
                    """.trimMargin()
                    thumbnail = guild.iconUrl
                }
            }
        )
    }
}
