package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class JoinRoomRequest implements Request {
    public final long roomId;
    public final long authToken;

    public JoinRoomRequest(long roomId, long authToken) {
        this.roomId = roomId;
        this.authToken = authToken;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.JOIN_ROOM;
    }
}
