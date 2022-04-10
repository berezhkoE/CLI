package ru.itmo.se.cli.util

import ru.itmo.se.cli.command.*
import ru.itmo.se.cli.exception.CommandNotFoundException
import ru.itmo.se.cli.exception.ExitCommandException

/**
 * Command Processor is used to interpret Tokens to Commands,
 * substitute variable values, and run Command execution
 */
class CliCommandProcessor {
    private val environment = CliEnvironment()

    /**
     * Runs execution of Commands in pipeline and prints output
     */
    fun executeCommands(commands: List<Command>) {
        val prevRes = collectPipelineResult(commands)
        if (prevRes != null) {
            println(prevRes)
        }
    }

    fun collectPipelineResult(commands: List<Command>): String? {
        var prevRes: String? = null
        for (command in commands) {
            if (prevRes != null) {
                command.prevOutput = prevRes
            }
            prevRes = command.execute()
        }
        return prevRes
    }

    /**
     * Interprets Tokens of each command in pipeline to list of Command.
     * Adds new variables to environment.
     * Performs substitution of variables values in arguments if needed
     */
    fun buildPipeline(tokens: List<List<Token>>): List<Command> {
        val commands: MutableList<Command> = ArrayList()
        for (command in tokens) {
            when (command.first().type) {
                TokenType.COMMAND -> commands.add(buildCommand(command))
                TokenType.VAR -> if (tokens.size == 1) resolveVarsDeclCommands(command)
                else -> throw CommandNotFoundException(command.first().content)
            }
        }
        return commands
    }

    /**
     * Processes variable declaration tokens and adds variables to the storage
     */
    private fun resolveVarsDeclCommands(tokens: List<Token>) {
        tokens
            .chunked(2)
            .forEach { (o1, o2) ->
                environment.putVariable(o1.content, resolveArgument(o2.content))
            }
    }

    private fun resolveArgument(content: String): String {
        return content
            .replace(Regex("\\$\\w+")) {
                if (notInsideSingleQuotes(it, content)) {
                    var nameEndIndex = content.indexOfAny(charArrayOf(' ', '\"'), it.range.first)
                    if (nameEndIndex == -1) {
                        nameEndIndex = content.length
                    }
                    val name = content.substring(it.range.first + 1, nameEndIndex)
                    environment.getVariable(name)
                } else content
            }
            .let { resolveSingleQuotes(it) }
            .let { resolveDoubleQuotes(it) }
    }

    private fun resolveDoubleQuotes(content: String): String {
        var result = content
        Regex("(\")[^\"]*(\")")
            .findAll(content)
            .map { match ->
                match.groups.toList()
                    .drop(1)
                    .map { it!!.range.first }
            }
            .flatten()
            .sortedDescending()
            .forEach {
                result = result.removeRange(it..it)
            }
        return result
    }

    private fun resolveSingleQuotes(content: String): String {
        var result = content
        Regex("\'[^\']*\'")
            .findAll(content)
            .map { it.range }
            .sortedByDescending { it.first }
            .forEach {
                result = result.removeRange(it.last..it.last)
                result = removeExtraSpaces(result, it.first + 1 until it.last)
                result = result.removeRange(it.first..it.first)
            }
        return result
    }

    private fun removeExtraSpaces(content: String, range: IntRange): String {
        val result = StringBuilder()
        var prevChar = ""
        for (i in content.indices) {
            val ch = content[i]
            if ((range.contains(i) && prevChar == " " && ch == ' ').not()) {
                result.append(ch)
            }
            prevChar = ch.toString()
        }
        return result.toString()
    }

    private fun notInsideSingleQuotes(match: MatchResult, content: String): Boolean {
        return content.substring(0 until match.range.first)
            .fold(0) { sum: Int, c: Char ->
                if ('\'' == c)
                    sum + 1
                else
                    sum
            } % 2 == 0
    }

    private fun buildCommand(tokens: List<Token>): Command {
        val command = tokens.first()
        val args = tokens.subList(1, tokens.size)
        return when (command.content) {
            "cat" -> Cat(tokensToArguments(args))
            "echo" -> Echo(tokensToArguments(args))
            "wc" -> Wc(tokensToArguments(args))
            "pwd" -> Pwd()
            "cd" -> Cd(tokensToArguments(args))
            "ls" -> Ls(tokensToArguments(args))
            "exit" -> throw ExitCommandException()
            else -> ExternalCommand(tokensToArguments(tokens).joinToString(" "))
        }
    }

    private fun tokensToArguments(tokens: List<Token>): List<String> {
        return tokens.map { resolveArgument(it.content) }
    }
}
