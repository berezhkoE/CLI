package ru.itmo.se.cli.command.clikit

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import java.io.BufferedReader
import java.util.regex.PatternSyntaxException

class Grep : CliktCommand() {
    private val ignoreCase by option("-i").flag(default = false)

    private val wordRegexp by option("-w").flag(default = false)

    private val afterContextNum by option("-A")
        .int()
        .restrictTo(min = 0)
        .default(0)

    private val pattern by argument()

    private val file by argument()
        .file()
        .convert {
            it.bufferedReader()
        }
        .default(BufferedReader(System.`in`.reader()))


    override fun run() {
        try {
            file.grep()
        } catch (_: PatternSyntaxException) {
            error("grep: Invalid regular expression")
        }
    }

    private fun BufferedReader.grep() {
        val regex = Regex(
            if (wordRegexp) "\\b$pattern\\b" else pattern,
            if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()
        )

        var count = 0

        this.forEachLine { line ->
            if (regex.containsMatchIn(line)) {
                echo(line)
                count = afterContextNum
            } else if (count > 0) {
                echo(line)
                count--
            }
        }
    }
}