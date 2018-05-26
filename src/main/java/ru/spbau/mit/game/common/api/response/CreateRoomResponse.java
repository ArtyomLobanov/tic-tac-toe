package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Room;

public class CreateRoomResponse implements Response {

    public final boolean success;

    public CreateRoomResponse(boolean success) {
        this.success = success;
    }

    @Override
    public API.Type getType() {
        return API.Type.CREATE_ROOM;
    }
}
