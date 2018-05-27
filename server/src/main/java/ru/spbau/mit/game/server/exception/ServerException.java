package ru.spbau.mit.game.server.exception;

public abstract class ServerException extends RuntimeException {
    public abstract int code();
    public abstract String message();
}
