package ru.itmo.se.cli

import ru.itmo.se.cli.util.CliCommandProcessor
import ru.itmo.se.cli.util.CliParser
import java.util.*


fun main() {
    val inputStream = System.`in`.reader()
    val outputStream = System.out.writer()

    val parser = CliParser()
    val commandProcessor = CliCommandProcessor()

    val scanner = Scanner(inputStream)
    while (scanner.hasNext()) {
        val commandList = commandProcessor.buildPipeline(
            parser.parseInput(scanner.nextLine())
        )
        val exitCode = commandProcessor.executeCommands(commandList, inputStream, outputStream)
        if (exitCode != 0) {
            break
        }
    }
}
