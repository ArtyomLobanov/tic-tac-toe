package ru.spbau.mit.game.server.exception;

public class NotFoundException extends ServerException {
    @Override
    public int code() {
        return 404;
    }

    @Override
    public String message() {
        return "Request type not supported";
    }
}
