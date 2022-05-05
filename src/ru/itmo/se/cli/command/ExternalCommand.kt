package ru.itmo.se.cli.command

import java.io.IOException
import java.io.Reader
import java.io.Writer


class ExternalCommand(private val command: String) : Command {
    override fun execute(input: Reader, output: Writer): Int {
        try {
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
            process.inputStream.bufferedReader().forEachLine { line ->
                output.appendLine(line)
                output.flush()
            }
            return 0
        } catch (e: IOException) {
            output.appendLine("$command: command not found")
            output.flush()
            return 1
        }
    }
}