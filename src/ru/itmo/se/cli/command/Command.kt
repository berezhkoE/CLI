package ru.itmo.se.cli.command

import java.io.Reader
import java.io.Writer

interface Command {
    fun execute(input: Reader, output: Writer): Int
}