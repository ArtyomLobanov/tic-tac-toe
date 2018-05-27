package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;

public class RegisterPlayerResponse implements Response {

    public final long id;
    public final long authToken;

    public RegisterPlayerResponse(long id, long authToken) {
        this.id = id;
        this.authToken = authToken;
    }

    @Override
    public API.Type getType() {
        return API.Type.REGISTER_PLAYER;
    }
}
