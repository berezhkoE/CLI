package ru.itmo.se.cli

import com.github.ajalt.clikt.core.UsageError
import ru.itmo.se.cli.exception.CliRuntimeException
import ru.itmo.se.cli.exception.ExitCommandException
import ru.itmo.se.cli.util.CliCommandProcessor
import ru.itmo.se.cli.util.CliParser
import java.util.*


fun main() {
    val scanner = Scanner(System.`in`)
    val parser = CliParser()
    val commandProcessor = CliCommandProcessor()

    while (scanner.hasNext()) {
        try {
            val commandList = commandProcessor.buildPipeline(
                parser.parseInput(scanner.nextLine())
            )
            commandProcessor.executeCommands(commandList)
        } catch (e: CliRuntimeException) {
            println(e.message)
        } catch (_: ExitCommandException) {
            break
        } catch (e: UsageError) {
            println(e.text)
        }
    }
}
