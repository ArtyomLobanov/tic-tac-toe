package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class GetRoomsListRequest implements Request {
    @Override
    public API.Type getDialogType() {
        return API.Type.GET_ROOMS;
    }
}
