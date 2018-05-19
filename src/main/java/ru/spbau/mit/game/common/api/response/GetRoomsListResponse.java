package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Room;

public class GetRoomsListResponse implements Response {
    public final Room[] rooms;

    public GetRoomsListResponse(Room[] rooms) {
        this.rooms = rooms;
    }

    @Override
    public API.Type getType() {
        return API.Type.GET_ROOMS;
    }
}
