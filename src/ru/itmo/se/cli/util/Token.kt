package ru.itmo.se.cli.util

data class Token(val content: String, val type: TokenType)

enum class TokenType {
    COMMAND,
    VAR,
    ARG;
}