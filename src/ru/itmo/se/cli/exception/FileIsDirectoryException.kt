package ru.itmo.se.cli.exception

class FileIsDirectoryException(command: String, file: String) : CliRuntimeException("$command: $file: Is a directory") {
}