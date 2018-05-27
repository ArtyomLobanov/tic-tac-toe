package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;

public class CreateRoomResponse implements Response {

    public final long id;

    public CreateRoomResponse(long id) {
        this.id = id;
    }

    @Override
    public API.Type getType() {
        return API.Type.CREATE_ROOM;
    }
}
