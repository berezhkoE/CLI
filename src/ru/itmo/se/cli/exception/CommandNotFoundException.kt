package ru.itmo.se.cli.exception

class CommandNotFoundException(command: String) : RuntimeException("$command: command not found")