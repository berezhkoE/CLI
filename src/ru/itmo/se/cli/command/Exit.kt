package ru.itmo.se.cli.command

import java.io.Reader
import java.io.Writer

class Exit : Command {
    override fun execute(input: Reader, output: Writer): Int {
        return 1
    }
}