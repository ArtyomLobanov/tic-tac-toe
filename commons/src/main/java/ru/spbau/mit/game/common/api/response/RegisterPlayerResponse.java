package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;

public class RegisterPlayerResponse implements Response {

    public final long id;

    public RegisterPlayerResponse(long id) {
        this.id = id;
    }

    @Override
    public API.Type getType() {
        return API.Type.REGISTER_PLAYER;
    }
}
