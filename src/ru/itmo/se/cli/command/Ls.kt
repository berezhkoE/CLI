package ru.itmo.se.cli.command

import ru.itmo.se.cli.exception.FileIsNotADirectoryException
import ru.itmo.se.cli.exception.NoSuchFileOrDirectoryException
import java.io.File


class Ls(private val args: List<String>) : Command() {
    override fun execute(): String {
        val directoryToList = args.firstOrNull() ?: System.getProperty("user.dir")
        val dirFile = File(directoryToList)
        if (!dirFile.exists()) {
            throw NoSuchFileOrDirectoryException("ls", directoryToList)
        }
        if (!dirFile.isDirectory) {
            throw FileIsNotADirectoryException("ls", directoryToList)
        }

        return dirFile
            .list { _, name -> !name.startsWith(".") }!!
            .joinToString("\n")
    }
}