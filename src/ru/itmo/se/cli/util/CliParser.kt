package ru.itmo.se.cli.util


class CliParser {
    private val commandRegex = Regex(
        "(\\w+=(?:[^ \"]*\"[^\"]*\"[^ \"]*)+)" +
                "|" +
                "(\\w+=(?:[^ \']*\'[^\']*\'[^ \']*)+)" +
                "|" +
                "(\\w+=\\S*)" +
                "|" +
                "((?:[^ \"]*\"[^\"]*\"[^ \"]*)+" +
                "|" +
                "(?:[^ ']*'[^']*'[^ ']*)+" +
                "|" +
                "\\$\\w+|\\S+)"
    )

    private val pipelineRegex = Regex("(?:[^\"|]*\"[^\"]*\"[^\"|]*)+|(?:[^\'|]*'[^\']*'[^\'|]*)+|[^|]+")

    fun parseInput(input: String): List<List<Token>> {
        return splitInputByPipeline(input).map { parseCommand(it) }
    }

    fun splitInputByPipeline(input: String): List<String> {
        return pipelineRegex.findAll(input).map { it.value }.toList()
    }

    private fun parseCommand(command: String): List<Token> {
        val matchResultSequence = commandRegexMatchResult(command)

        val map = matchResultSequence.map { it.groupValues[4] }.toList()
        val indexOfFirst = map.indexOfFirst { it.isNotEmpty() }
        if (indexOfFirst != -1) {
            val strings = matchResultSequence.map { it.value }.toList()
            val tokens = mutableListOf(
                Token(map[indexOfFirst], TokenType.COMMAND)
            )
            tokens.addAll(strings.subList(indexOfFirst + 1, strings.size).map { Token(it, TokenType.ARG) })
            return tokens
        }

        return matchResultSequence
            .map {
                val indexOfEq = it.value.indexOf('=')
                listOf(
                    Token(it.value.substring(0 until indexOfEq), TokenType.VAR),
                    Token(it.value.substring(indexOfEq + 1), TokenType.ARG)
                )
            }
            .flatten()
            .toList()
    }

    fun commandRegexMatchResult(command: String): Sequence<MatchResult> {
        return commandRegex.findAll(command)
    }
}
