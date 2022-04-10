package ru.itmo.se.cli.command.clikit

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.defaultStdin
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import ru.itmo.se.cli.exception.FileIsDirectoryException
import ru.itmo.se.cli.exception.NoSuchFileOrDirectoryException
import java.io.InputStream
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
            if (it.isDirectory) {
                throw FileIsDirectoryException("grep", it.name)
            }
            if (!it.isFile) {
                throw NoSuchFileOrDirectoryException("grep", it.name)
            }
            it.inputStream() as InputStream
        }
        .defaultStdin()

    override fun run() {
        try {
            val regex = Regex(
                if (wordRegexp) "\b$pattern\b" else pattern,
                if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()
            )

            var count = 0
            file.bufferedReader().useLines {
                it.forEach { line ->
                    if (regex.containsMatchIn(line)) {
                        echo(line)
                        count = afterContextNum
                    } else if (count > 0) {
                        echo(line)
                        count--
                    }
                }
            }
        } catch (_: PatternSyntaxException) {
            echo("grep: Invalid regular expression")
        }
    }
}