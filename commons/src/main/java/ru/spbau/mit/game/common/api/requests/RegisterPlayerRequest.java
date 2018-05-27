package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class RegisterPlayerRequest implements Request {

    public final String name;
    public final String password;

    public RegisterPlayerRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.REGISTER_PLAYER;
    }
}
