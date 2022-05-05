package ru.itmo.se.cli.command

import java.io.Reader
import java.io.Writer

class Echo(private val args: List<String>) : Command {
    override fun execute(input: Reader, output: Writer): Int {
        output.appendLine(args.joinToString(" "))
        output.flush()
        return 0
    }
}