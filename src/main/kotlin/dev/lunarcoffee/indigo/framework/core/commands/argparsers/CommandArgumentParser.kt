package dev.lunarcoffee.indigo.framework.core.commands.argparsers

interface CommandArgumentParser {
    fun split(): List<String>
}
