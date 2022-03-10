package ru.itmo.se.cli.command

import ru.itmo.se.cli.exception.CommandNotFoundException
import java.io.IOException


class ExternalCommand(private val command: String) : Command() {
    override fun execute(): String {
        val output: MutableList<String> = ArrayList()
        try {
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
            process.inputStream.bufferedReader().useLines {
                it.map { line -> output.add(line) }
            }
        } catch (e: IOException) {
            throw CommandNotFoundException(command)
        }
        return output.joinToString("\n")
    }
}