package ru.itmo.se.cli.command

import ru.itmo.se.cli.exception.FileIsNotADirectoryException
import ru.itmo.se.cli.exception.NoSuchFileOrDirectoryException
import java.io.File


class Cd(private val args: List<String>) : Command() {
    override fun execute(): String {
        val homeDir = args.firstOrNull() ?: System.getProperty("user.home")
        val dirFile = File(homeDir)
        if (!dirFile.exists()) {
            throw NoSuchFileOrDirectoryException("cd", homeDir)
        }
        if (!dirFile.isDirectory) {
            throw FileIsNotADirectoryException("cd", homeDir)
        }

        System.setProperty("user.dir", dirFile.absolutePath)

        return ""
    }
}