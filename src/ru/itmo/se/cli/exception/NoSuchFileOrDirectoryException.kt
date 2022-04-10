package ru.itmo.se.cli.exception

class NoSuchFileOrDirectoryException(command: String, file: String) :
    CliRuntimeException("$command: $file: No such file or directory")