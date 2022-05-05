package ru.itmo.se.cli.util

/**
 * Command parser
 */
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
                "\\$\\w+|\\S+)(?:(?=\\s)|\$)"
    )

    private val pipelineRegex = Regex("(?:[^\"|]*\"[^\"]*\"[^\"|]*)+|(?:[^\'|]*'[^\']*'[^\'|]*)+|[^|]+")

    /**
     * Splits input by pipeline symbol and parses each command in pipeline
     * @return List<List<Token>> - list of Tokens for each command in pipeline
     */
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
            .flatMap {
                it.value.split("=", limit = 2).let { (f, s) ->
                    sequenceOf(Token(f, TokenType.VAR), Token(s, TokenType.ARG))
                }
            }
            .toList()
    }

    fun commandRegexMatchResult(command: String): Sequence<MatchResult> {
        return commandRegex.findAll(command)
    }
}
