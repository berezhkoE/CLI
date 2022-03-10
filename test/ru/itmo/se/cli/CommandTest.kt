package ru.itmo.se.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.itmo.se.cli.command.Wc

class CommandTest {
    @Test
    fun testWc() {
        Assertions.assertEquals(
            "  288  2064 23664 WcInput.txt",
            Wc(listOf("testResources/WcInput.txt")).statsForArgs()
        )
    }
}