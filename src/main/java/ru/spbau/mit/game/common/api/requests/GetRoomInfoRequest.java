package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class GetRoomInfoRequest implements Request {
    public final long roomId;

    public GetRoomInfoRequest(long roomId) {
        this.roomId = roomId;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.GET_ROOM_INFO;
    }
}
