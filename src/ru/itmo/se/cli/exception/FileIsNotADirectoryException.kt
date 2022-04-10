package ru.itmo.se.cli.exception

class FileIsNotADirectoryException(command: String, file: String) : CliRuntimeException("$command: not a directory: $file") {
}