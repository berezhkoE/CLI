package ru.itmo.se.cli.command

import java.io.File
import java.nio.charset.StandardCharsets


class Wc(private val args: List<String>) : Command() {
    private var totalByteCount = 0L
    private var totalLineCount = 0L
    private var totalWordCount = 0L
    private var wordCount = 0
    private var lineCount = 0
    private var byteCount = 0L

    override fun execute(): String {
        if (args.isNotEmpty()) {
            return statsForArgs()
        }
        return prevOutput?.let { statsForPrevOutput(it) } ?: ""
    }

    private fun statsForPrevOutput(input: String): String {
        byteCount = input.toByteArray(StandardCharsets.UTF_8).size.toLong()
        wordCount = input.split("\\s+").size
        lineCount = Regex("[\r\n]").findAll(input).count()

        return "$lineCount  $wordCount  ${byteCount + 1}"
    }

    fun statsForArgs(): String {
        val result: MutableList<MutableList<String>> = ArrayList()
        for (str in args) {
            if (str.isNotBlank()) {
                val file = File(str)
                byteCount = file.length()
                totalByteCount += byteCount

                wordCount = 0
                lineCount = 0
                File(str).bufferedReader().useLines {
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
            }
        }

        if (result.size > 1) {
            val totalRes = mutableListOf(totalLineCount, totalWordCount, totalByteCount)
                .map { it.toString() }
                .toMutableList()
            totalRes.add("total")
            result.add(totalRes)
        }

        val pad = result.map { list ->
            list.dropLast(1)
                .map {
                    it.length
                }
        }.toList().flatten().maxOrNull() ?: 0

        return result.joinToString("\n") { line ->
            line.joinToString(" ") {
                it.padStart(pad)
            }
        }
    }
}