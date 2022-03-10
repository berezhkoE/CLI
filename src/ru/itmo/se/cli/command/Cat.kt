package ru.itmo.se.cli.command

import java.io.File


class Cat(private val args: List<String>) : Command() {
    override fun execute(): String {
        if (args.isNotEmpty()) {
            val output: MutableList<String> = ArrayList()
            for (str in args) {
                if (str.isNotBlank()) {
                    File(str).bufferedReader().useLines {
                        it.map { line -> output.add(line) }
                    }
                }
            }
            return output.joinToString("\n")
        }
        return prevOutput.orEmpty()
    }
}