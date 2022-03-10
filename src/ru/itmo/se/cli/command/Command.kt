package ru.itmo.se.cli.command

abstract class Command {
    var prevOutput: String? = null

    abstract fun execute(): String
}