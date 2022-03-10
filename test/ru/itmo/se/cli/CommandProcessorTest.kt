package ru.itmo.se.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.itmo.se.cli.util.CliCommandProcessor
import ru.itmo.se.cli.util.Token
import ru.itmo.se.cli.util.TokenType

class CommandProcessorTest {
    private val commandProcessor = CliCommandProcessor()

    @Test
    fun testCommandProcessor() {
        commandProcessor.buildPipeline(
            listOf(
                listOf(
                    Token("x", TokenType.VAR), Token("\'first     value\'", TokenType.ARG),
                    Token("y", TokenType.VAR), Token("\"second   value\"", TokenType.ARG)
                )
            )
        )

        val inputTokens = listOf(
            listOf(
                Token("x", TokenType.VAR), Token("123", TokenType.ARG),
                Token("y", TokenType.VAR), Token("\"4   56\"", TokenType.ARG)
            ),
            listOf(
                Token("echo", TokenType.COMMAND),
                Token("\$x", TokenType.ARG),
                Token("\$y", TokenType.ARG)
            ),
            listOf(
                Token("cat", TokenType.COMMAND)
            )
        )

        val commandList = commandProcessor.buildPipeline(inputTokens)
        val expected = "first value second   value"

        Assertions.assertEquals(expected, commandProcessor.collectPipelineResult(commandList))
    }
}