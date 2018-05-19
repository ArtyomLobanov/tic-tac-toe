package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Room;

public class GetRoomInfoResponse implements Response {

    public final Room room;

    public GetRoomInfoResponse(Room room) {
        this.room = room;
    }

    @Override
    public API.Type getType() {
        return API.Type.GET_ROOM_INFO;
    }
}
