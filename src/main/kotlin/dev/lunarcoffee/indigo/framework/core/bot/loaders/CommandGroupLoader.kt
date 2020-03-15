package dev.lunarcoffee.indigo.framework.core.bot.loaders

import dev.lunarcoffee.indigo.framework.core.commands.Command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup

class CommandGroupLoader(sourceRoot: String) : BotComponentLoader<Map<String, List<Command>>>(sourceRoot) {
    override fun load() = reflections
        .getTypesAnnotatedWith(CommandGroup::class.java)
        .map {
            Pair(
                (it.annotations.find { a -> a is CommandGroup } as? CommandGroup)?.name,
                it.constructors.getOrNull(0)?.newInstance()
            )
        }
        .filter { it.first != null && it.second != null }
        .map {
            Pair(
                it.first!!,
                it.second!!::class.java
                    .methods
                    .filter { m -> m.returnType == Command::class.java }
                    .map { m -> m.invoke(it.second) as Command }
            )
        }
        .sortedBy { it.first }
        .toMap()
}
