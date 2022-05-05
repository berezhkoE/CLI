package ru.itmo.se.cli.command

import java.io.File
import java.io.FileNotFoundException
import java.io.Reader
import java.io.Writer
import java.nio.charset.StandardCharsets


class Wc(private val args: List<String>) : Command {
    private val newLineRegex = Regex("[\r\n]")

    private var totalByteCount = 0L
    private var totalLineCount = 0L
    private var totalWordCount = 0L
    private var wordCount = 0
    private var lineCount = 0
    private var byteCount = 0L

    override fun execute(input: Reader, output: Writer): Int {
        if (args.isNotEmpty()) {
            return statsForArgs(output)
        }
        return statsForPrevOutput(input, output)
    }

    private fun statsForPrevOutput(input: Reader, output: Writer): Int {
        byteCount = input.readText().toByteArray(StandardCharsets.UTF_8).size.toLong()
        wordCount = input.readText().split("\\s+").size
        lineCount = newLineRegex.findAll(input.readText()).count()

        output.appendLine("$lineCount  $wordCount  ${byteCount + 1}")
        output.flush()

        return 0
    }

    fun statsForArgs(output: Writer): Int {
        val result: MutableList<MutableList<String>> = ArrayList()
        for (str in args) {
            if (str.isNotBlank()) {
                val file = File(str)
                try {
                    byteCount = file.length()
                    totalByteCount += byteCount

                    wordCount = 0
                    lineCount = 0
                    file.bufferedReader().useLines {
                        it.forEach { line ->
                            if (line.isNotEmpty()) {
                                wordCount += line.split(Regex("\\s+")).size
                            }
                            lineCount += 1
                        }
                    }

                    totalLineCount += lineCount
                    totalWordCount += wordCount

                    val argRes = mutableListOf(lineCount, wordCount, byteCount)
                        .map { it.toString() }
                        .toMutableList()
                    argRes.add(file.name)
                    result.add(argRes)
                } catch (_: FileNotFoundException) {
                    if (file.isDirectory) {
                        output.appendLine("wc: $str: Is a directory")
                        output.flush()
                        return 1
                    }
                    if (!file.isFile) {
                        output.appendLine("wc: $str: No such file or directory")
                        output.flush()
                        return 1
                    }
                }
            }
        }

        if (result.size > 1) {
            val totalRes = mutableListOf(totalLineCount, totalWordCount, totalByteCount)
                .map { it.toString() }
                .toMutableList()
            totalRes.add("total")
            result.add(totalRes)
        }

        val pad = result.flatMap { list ->
            list.dropLast(1)
                .map {
                    it.length
                }
        }.toList().maxOrNull() ?: 0

        result.forEachIndexed { index, line ->
            val value = line.joinToString(" ") {
                it.padStart(pad)
            }
            if (index == result.lastIndex) {
                output.append(value)
            } else {
                output.appendLine(value)
            }
        }

        return 0
    }
}