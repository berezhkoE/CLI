package ru.itmo.se.cli.exception

class UnexpectedTokenError(content: String) : Error("$content: unexpected token")