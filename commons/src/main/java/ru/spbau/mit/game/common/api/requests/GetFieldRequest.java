package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class GetFieldRequest implements Request {
    public final long roomId;

    public GetFieldRequest(long roomId) {
        this.roomId = roomId;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.GET_FIELD;
    }
}
