package ru.itmo.se.cli.command

import java.io.File
import java.io.FileNotFoundException
import java.io.Reader
import java.io.Writer


class Cat(private val args: List<String>) : Command {
    override fun execute(input: Reader, output: Writer): Int {
        if (args.isNotEmpty()) {
            for (str in args) {
                if (str.isNotBlank()) {
                    val file = File(str)
                    try {
                        file.bufferedReader().forEachLine { line ->
                            output.appendLine(line)
                            output.flush()
                        }
                    } catch (_: FileNotFoundException) {
                        if (file.isDirectory) {
                            output.appendLine("cat: $str: Is a directory")
                            output.flush()
                            return 1
                        }
                        if (!file.isFile) {
                            output.appendLine("cat: $str: No such file or directory")
                            output.flush()
                            return 1
                        }
                    }
                }
            }
            return 0
        }
        input.forEachLine { line ->
            output.appendLine(line)
            output.flush()
        }
        return 0
    }
}