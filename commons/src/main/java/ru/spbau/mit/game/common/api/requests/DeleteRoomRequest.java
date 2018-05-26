package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class DeleteRoomRequest implements Request {

    public final long roomId;
    public final long password;

    public DeleteRoomRequest(long roomId, long password) {
        this.roomId = roomId;
        this.password = password;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.DELETE_ROOM;
    }
}
