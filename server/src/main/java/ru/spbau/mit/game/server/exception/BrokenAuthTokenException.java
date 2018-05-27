package ru.spbau.mit.game.server.exception;

public class BrokenAuthTokenException extends ServerException {
    @Override
    public int code() {
        return 401;
    }

    @Override
    public String message() {
        return "Wrong authorization token";
    }
}
