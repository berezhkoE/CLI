package ru.itmo.se.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.itmo.se.cli.util.CliCommandProcessor
import ru.itmo.se.cli.util.Token
import ru.itmo.se.cli.util.TokenType
import java.io.Reader
import java.io.StringWriter
import java.io.Writer

class CommandProcessorTest {
    private lateinit var commandProcessor: CliCommandProcessor

    private lateinit var reader: Reader
    private lateinit var writer: Writer

    @BeforeEach
    fun setup() {
        commandProcessor = CliCommandProcessor()
        reader = System.`in`.reader()
        writer = StringWriter()
    }

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
        val expected = "first value second   value\n"
        commandProcessor.executeCommands(commandList, reader, writer)
        Assertions.assertEquals(expected, writer.toString())
    }

    @Test
    fun testCommandToVarAssign() {
        commandProcessor.buildPipeline(
            listOf(
                listOf(
                    Token("x", TokenType.VAR), Token("ec", TokenType.ARG),
                    Token("y", TokenType.VAR), Token("ho", TokenType.ARG)
                )
            )
        )

        val inputTokens = listOf(
            listOf(
                Token("\$x\$y", TokenType.COMMAND), Token("1", TokenType.ARG)
            )
        )

        val commandList = commandProcessor.buildPipeline(inputTokens)
        val expectedCommand = "echo"
        Assertions.assertEquals(expectedCommand, commandList[0].javaClass.simpleName.lowercase())

        val expectedResult = "1\n"
        commandProcessor.executeCommands(commandList, reader, writer)
        Assertions.assertEquals(expectedResult, writer.toString())
    }

    @Test
    fun testCommandWithQuotes() {
        commandProcessor.buildPipeline(
            listOf(
                listOf(
                    Token("x", TokenType.VAR), Token("echo", TokenType.ARG)
                )
            )
        )

        val inputTokens = listOf(
            listOf(
                Token("\'\$x\'", TokenType.COMMAND), Token("1", TokenType.ARG)
            )
        )

        val commandList = commandProcessor.buildPipeline(inputTokens)
        val expectedCommand = "echo"
        Assertions.assertEquals(expectedCommand, commandList[0].javaClass.simpleName.lowercase())

        val expectedResult = "1\n"
        commandProcessor.executeCommands(commandList, reader, writer)
        Assertions.assertEquals(expectedResult, writer.toString())
    }
}