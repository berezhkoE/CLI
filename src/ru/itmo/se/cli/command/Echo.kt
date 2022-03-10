package ru.itmo.se.cli.command

class Echo(private val args: List<String>) : Command() {
    override fun execute(): String {
        if (args.isEmpty()) {
            return ""
        }
        val result: MutableList<String> = ArrayList()
        for (s in args) {
            result.add(s)
        }
        return result.joinToString(" ")
    }
}