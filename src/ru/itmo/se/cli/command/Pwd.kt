package ru.itmo.se.cli.command

import java.io.Reader
import java.io.Writer

class Pwd : Command {
    override fun execute(input: Reader, output: Writer): Int {
        output.appendLine(System.getProperty("user.dir"))
        output.flush()
        return 0
    }
}