package ru.itmo.se.cli.command.clikit

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktConsole
import ru.itmo.se.cli.command.Command

class CliktCommandAdapter(private val command: CliktCommand, private val args: List<String>) : Command() {
    override fun execute(): String {
        val result: MutableList<String> = ArrayList()
        command.context {
            console = object : CliktConsole {
                override val lineSeparator: String get() = ""

                override fun print(text: String, error: Boolean) {
                    result.add(text)
                }

                override fun promptForLine(prompt: String, hideInput: Boolean): String? {
                    return null
                }
            }
        }
        command.parse(args)
        return result.joinToString("\n")
    }
}