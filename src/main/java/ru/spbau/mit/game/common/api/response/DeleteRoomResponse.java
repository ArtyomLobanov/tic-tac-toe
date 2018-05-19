package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;

public class DeleteRoomResponse implements Response {
    @Override
    public API.Type getType() {
        return API.Type.DELETE_ROOM;
    }
}
