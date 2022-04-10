package ru.itmo.se.cli.command

import ru.itmo.se.cli.exception.FileIsDirectoryException
import ru.itmo.se.cli.exception.NoSuchFileOrDirectoryException
import java.io.File
import java.io.FileNotFoundException


class Cat(private val args: List<String>) : Command() {
    override fun execute(): String {
        if (args.isNotEmpty()) {
            val output: MutableList<String> = ArrayList()
            for (str in args) {
                if (str.isNotBlank()) {
                    val file = File(str)
                    try {
                        file.bufferedReader().useLines {
                            it.map { line -> output.add(line) }
                        }
                    } catch (_: FileNotFoundException) {
                        if (file.isDirectory) {
                            throw FileIsDirectoryException("cat", str)
                        }
                        if (!file.isFile) {
                            throw NoSuchFileOrDirectoryException("cat", str)
                        }
                    }
                }
            }
            return output.joinToString("\n")
        }
        return prevOutput.orEmpty()
    }
}