package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;

public class DeleteRoomResponse implements Response {

    public final boolean success;

    public DeleteRoomResponse(boolean success) {
        this.success = success;
    }

    @Override
    public API.Type getType() {
        return API.Type.DELETE_ROOM;
    }
}
