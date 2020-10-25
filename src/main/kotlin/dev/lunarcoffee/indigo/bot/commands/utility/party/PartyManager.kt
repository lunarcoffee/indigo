package dev.lunarcoffee.indigo.bot.commands.utility.party

import net.dv8tion.jda.api.entities.Member

object PartyManager {
    private val parties = mutableMapOf<String, List<Member>>()

    fun joinOrCreate(name: String, member: Member): Unit? {
        val party = parties[name] ?: emptyList()
        if (member in party)
            return null

        parties[name] = party + member
        return Unit
    }

    fun leave(name: String, member: Member): Unit? {
        val party = parties[name]?:return null
        if (member !in party)
            return null

        parties[name] = party - member
        if (parties[name]!!.isEmpty())
            disband(name, null)
        return Unit
    }

    fun disband(name: String, member: Member?): Boolean? {
        val party = parties[name] ?: return null
        if (member != null && member !in party)
            return false

        parties -= name
        return true
    }

    fun get(name: String) = parties[name]
}
