package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class GetFieldPatchRequest implements Request {

    public final long roomId;
    public final int startVersion;

    public GetFieldPatchRequest(long roomId, int startVersion) {
        this.roomId = roomId;
        this.startVersion = startVersion;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.GET_FIELD_PATCH;
    }
}
