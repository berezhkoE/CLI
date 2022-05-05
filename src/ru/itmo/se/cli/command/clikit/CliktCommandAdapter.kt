package ru.itmo.se.cli.command.clikit

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktConsole
import ru.itmo.se.cli.command.Command
import java.io.Reader
import java.io.Writer

class CliktCommandAdapter(private val command: CliktCommand, private val args: List<String>) : Command {
    override fun execute(input: Reader, output: Writer): Int {
        command.context {
            console = object : CliktConsole {
                override val lineSeparator: String get() = "\n"

                override fun print(text: String, error: Boolean) {
                    output.append(text)
                }

                override fun promptForLine(prompt: String, hideInput: Boolean): String? {
                    return null
                }
            }
        }
        command.parse(args)
        output.flush()
        return 0
    }
}