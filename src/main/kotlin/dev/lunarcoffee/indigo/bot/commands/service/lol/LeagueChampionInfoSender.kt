package dev.lunarcoffee.indigo.bot.commands.service.lol

import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.staticdata.Champion
import com.merakianalytics.orianna.types.core.staticdata.Champions
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.distance
import dev.lunarcoffee.indigo.framework.api.dsl.paginator
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

class LeagueChampionInfoSender(private val championNames: List<String>) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        // Try to get the champion given [championNames]. If that fails, take the first champion with distance less
        // than four from any of the [championNames]. If that fails, error out.
        val champion = championNames
            .map { Champion.named(it).withRegion(Region.NORTH_AMERICA).get() }
            .firstOrNull { it.stats != null }
            ?: allChampions.firstOrNull { champion -> championNames.any { it.distance(champion.name) < 4 } }
        ctx.checkNull(champion, "That champion does not exist!") ?: return

        ctx.send(
            ctx.paginator {
                val embedTitle = "${Emoji.BOOK}  Info on **${champion!!.name}**, ${champion.title}:"
                embedPage {
                    title = embedTitle
                    description = champion.lore
                    thumbnail = champion.image.url
                }
                embedPage {
                    title = embedTitle
                    thumbnail = champion.image.url

                    val stats = champion.stats
                    description = """
                        |**Health**: ${stats.health} (+${stats.healthPerLevel})
                        |**Health regen**: ${stats.healthRegen} (+${stats.healthRegenPerLevel})
                        |**Mana**: ${stats.mana} (+${stats.manaPerLevel})
                        |**Mana regen**: ${stats.manaRegen} (+${stats.manaRegenPerLevel})
                        |**Movement speed**: ${stats.movespeed}
                        |**Attack damage**: ${stats.attackDamage} (+${stats.attackDamagePerLevel})
                        |**Attack speed per level**: +${stats.attackSpeedPerLevel}%
                        |**Attack range**: ${stats.attackRange}
                        |**Armor**: ${stats.armor} (+${stats.armorPerLevel})
                        |**Magic resist**: ${stats.magicResist} (+${stats.magicResistPerLevel})
                        |**Crit chance**: ${stats.criticalStrikeChance}% (+${stats.criticalStrikeChancePerLevel}%)
                    """.trimMargin()
                }
                embedPage {
                    title = embedTitle
                    thumbnail = champion.image.url

                    field("Passive: ${champion.passive.name}", champion.passive.description().removeHtmlTags())
                    for ((spell, key) in champion.spells.zip(listOf('Q', 'W', 'E', 'R'))) {
                        field {
                            name = "$key: ${spell.name}"
                            value = """
                                |**Cooldown**: ${spell.cooldowns.formatDistinct()} seconds
                                |**Range**: ${spell.ranges.formatDistinct()} units
                                |**Description**: ${spell.description.removeHtmlTags()}
                            """.trimMargin()
                        }
                    }
                }
            }
        )
    }

    private fun List<Number>.formatDistinct() = if (distinct().size == 1) get(0).toString() else joinToString("/")
    private fun String.removeHtmlTags() = replace("(<br>)+".toRegex(), " ").replace("<[^<>]+>".toRegex(), "")

    companion object {
        private val allChampions = Champions.withRegion(Region.NORTH_AMERICA).get()
    }
}