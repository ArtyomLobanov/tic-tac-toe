package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class DeleteRoomRequest implements Request {

    public long roomId;
    public long password;

    @Override
    public API.Type getDialogType() {
        return API.Type.DELETE_ROOM;
    }
}
