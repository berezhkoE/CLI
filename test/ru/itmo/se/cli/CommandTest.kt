package ru.itmo.se.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.itmo.se.cli.command.*
import ru.itmo.se.cli.command.clikit.CliktCommandAdapter
import ru.itmo.se.cli.command.clikit.Grep
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer

class CommandTest {
    private lateinit var reader: Reader
    private lateinit var writer: Writer

    @BeforeEach
    fun setup() {
        reader = System.`in`.reader()
        writer = StringWriter()
    }

    @Test
    fun testWc() {
        Wc(listOf("testResources/WcInput.txt")).statsForArgs(writer)

        Assertions.assertEquals(
            "  288  2064 23664 WcInput.txt",
            writer.toString()
        )
    }

    @Test
    fun testCatFromFile() {
        Cat(listOf("testResources/CatInput.txt")).execute(reader, writer)

        Assertions.assertEquals(
            "Реализовать простой интерпретатор командной строки\n",
            writer.toString()
        )
    }

    @Test
    fun testCatFromInput() {
        val testString = "Реализовать простой интерпретатор командной строки\n"

        reader = StringReader(testString)
        Cat(emptyList()).execute(reader, writer)

        Assertions.assertEquals(
            testString,
            writer.toString()
        )
    }

    @Test
    fun testEcho() {
        val testString = "Реализовать простой интерпретатор командной строки"

        Echo(listOf(testString)).execute(reader, writer)

        Assertions.assertEquals(
            testString,
            writer.toString().trim()
        )
    }

    @Test
    fun testExternalCommand() {
        ExternalCommand("git").execute(reader, writer)

        Assertions.assertEquals(
            "usage: git [--version] [--help] [-C <path>] [-c <name>=<value>]",
            writer.toString().substringBefore("\n")
        )
    }

    @Test
    fun testPwd() {
        Pwd().execute(reader, writer)
        Assertions.assertEquals(
            System.getProperty("user.dir"),
            writer.toString().trim()
        )
    }

    @Test
    fun testGrep() {
        CliktCommandAdapter(Grep(), listOf("exit", "testResources/GrepInput.txt")).execute(reader, writer)
        Assertions.assertEquals(
            "exit - выходит из интерпретатора",
            writer.toString().trim()
        )
    }
}