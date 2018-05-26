package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;

public class JoinRoomResponse implements Response {

    public final boolean success;

    public JoinRoomResponse(boolean success) {
        this.success = success;
    }

    @Override
    public API.Type getType() {
        return API.Type.JOIN_ROOM;
    }
}
