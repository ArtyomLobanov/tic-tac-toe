package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;

public class UpdateFieldResponse implements Response {

    public final boolean success;

    public UpdateFieldResponse(boolean success) {
        this.success = success;
    }

    @Override
    public API.Type getType() {
        return API.Type.UPDATE_FIELD;
    }
}
