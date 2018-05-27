package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Room;

import java.util.List;

public class GetRoomsListResponse implements Response {
    public final List<Room> rooms;

    public GetRoomsListResponse(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public API.Type getType() {
        return API.Type.GET_ROOMS;
    }
}
