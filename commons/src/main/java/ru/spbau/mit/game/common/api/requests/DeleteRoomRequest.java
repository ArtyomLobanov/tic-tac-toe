package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class DeleteRoomRequest implements Request {

    public final long roomId;
    public final long authToken;

    public DeleteRoomRequest(long roomId, long authToken) {
        this.roomId = roomId;
        this.authToken = authToken;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.DELETE_ROOM;
    }
}
