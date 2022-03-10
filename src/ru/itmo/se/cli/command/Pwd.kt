package ru.itmo.se.cli.command

class Pwd : Command() {
    override fun execute(): String {
        return System.getProperty("user.dir")
    }
}