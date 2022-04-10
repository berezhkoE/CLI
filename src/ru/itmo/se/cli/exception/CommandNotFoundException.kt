package ru.itmo.se.cli.exception

class CommandNotFoundException(command: String) : CliRuntimeException("$command: command not found")