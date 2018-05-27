package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class GetRoomsListRequest implements Request {

    public final int startPosition;
    public final int limit;

    public GetRoomsListRequest(int startPosition, int limit) {
        this.startPosition = startPosition;
        this.limit = limit;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.GET_ROOMS;
    }
}
