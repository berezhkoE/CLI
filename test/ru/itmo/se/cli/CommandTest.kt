package ru.itmo.se.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.itmo.se.cli.command.Cd
import ru.itmo.se.cli.command.Ls
import ru.itmo.se.cli.command.Wc
import ru.itmo.se.cli.exception.FileIsNotADirectoryException
import ru.itmo.se.cli.exception.NoSuchFileOrDirectoryException
import java.io.File

class CommandTest {
    @Test
    fun testWc() {
        Assertions.assertEquals(
            "  288  2064 23664 WcInput.txt",
            Wc(listOf("testResources/WcInput.txt")).statsForArgs()
        )
    }

    @Test
    fun testCd() {
        val expectedWorkDir = File("testResources")

        Cd(listOf("testResources")).execute()

        Assertions.assertEquals(
            expectedWorkDir.absolutePath,
            System.getProperty("user.dir")
        )
    }

    @Test
    fun testCdThrowsExceptionWhenDirectoryNotExists() {
        Assertions.assertThrows(NoSuchFileOrDirectoryException::class.java) {
            Cd(listOf("some_dir_which_not_exists")).execute()
        }
    }

    @Test
    fun testCdThrowsWhenFileIsNotADirectory() {
        Assertions.assertThrows(FileIsNotADirectoryException::class.java) {
            Cd(listOf("testResources/WcInput.txt")).execute()
        }
    }

    @Test
    fun testLs() {
        Assertions.assertEquals(
            "WcInput.txt",
            Ls(listOf("testResources")).execute()
        )
    }

    @Test
    fun testLsThrowsExceptionWhenDirectoryNotExists() {
        Assertions.assertThrows(NoSuchFileOrDirectoryException::class.java) {
            Ls(listOf("some_dir_which_not_exists")).execute()
        }
    }

    @Test
    fun testLsThrowsWhenFileIsNotADirectory() {
        Assertions.assertThrows(FileIsNotADirectoryException::class.java) {
            Ls(listOf("testResources/WcInput.txt")).execute()
        }
    }
}