package ru.itmo.se.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.itmo.se.cli.util.CliParser
import ru.itmo.se.cli.util.Token
import ru.itmo.se.cli.util.TokenType

class ParserTest {
    private val parser = CliParser()

    @Test
    fun testSplitInputByPipeline() {
        val expected = listOf(
            "x=123'   |   '13' '1' 2 2'  y=123 ",
            " echo 123\"  |    \"13\"     \"1\" 2 2\" \"\"  ",
            " echo   \$x "
        )
        val input = expected.joinToString("|")

        Assertions.assertLinesMatch(expected, parser.splitInputByPipeline(input))
    }

    @Test
    fun testCommandRegex() {
        val input = "   echo \"a=\"    i==12222\"   =21\$2 213\" b='12  3'  \"123\"   j=4256122222  x=1=1qwe echo ew"

        val matchResultSequence = parser.commandRegexMatchResult(input).map { it.groupValues }

        val groupExpectedMatch = listOf(
            listOf("i==12222\"   =21\$2 213\""),
            listOf("b='12  3'"),
            listOf("j=4256122222", "x=1=1qwe"),
            listOf("echo", "\"a=\"", "\"123\"", "echo", "ew")
        )

        groupExpectedMatch.forEachIndexed { i, expected ->
            Assertions.assertLinesMatch(
                expected,
                matchResultSequence.map { it[i + 1] }.filterNot { it.isEmpty() }.toList()
            )
        }
    }

    @Test
    fun testParser() {
        val input = "x=123   y=\"4   56\" | echo \$x | cat"

        val expected = listOf(
            listOf(
                Token("x", TokenType.VAR), Token("123", TokenType.ARG),
                Token("y", TokenType.VAR), Token("\"4   56\"", TokenType.ARG)
            ),
            listOf(
                Token("echo", TokenType.COMMAND),
                Token("\$x", TokenType.ARG)
            ),
            listOf(
                Token("cat", TokenType.COMMAND)
            )
        )

        Assertions.assertTrue(expected == parser.parseInput(input))
    }

    @Test
    fun testCommandWithSeveralDollarSigns() {
        val input = "\$x\$y\$z 1"

        val expected = listOf(
            listOf(
                Token("\$x\$y\$z", TokenType.COMMAND),
                Token("1", TokenType.ARG)
            )
        )

        Assertions.assertTrue(expected == parser.parseInput(input))
    }
}