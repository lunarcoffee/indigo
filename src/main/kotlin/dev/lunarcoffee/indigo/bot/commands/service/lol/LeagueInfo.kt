package dev.lunarcoffee.indigo.bot.commands.service.lol

import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.staticdata.*

object LeagueInfo {
    val allChampions = Champions.withRegion(Region.NORTH_AMERICA).get()!!
    val allItems = Items.withRegion(Region.NORTH_AMERICA).get()!!
    val allRunes = ReforgedRunes.withRegion(Region.NORTH_AMERICA).get()!!
    
    val lineBreakRegex = "(<br>)+".toRegex()
    val tagRegex = "<[^>]+>".toRegex()
}
