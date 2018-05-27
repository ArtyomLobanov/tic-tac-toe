package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.GameType;

public class CreateRoomRequest implements Request {

    public final String roomName;
    public final GameType type;
    public final long authToken;
    public final boolean hostStarts;

    public CreateRoomRequest(String roomName, GameType type, long authToken, boolean hostStarts) {
        this.roomName = roomName;
        this.type = type;
        this.authToken = authToken;
        this.hostStarts = hostStarts;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.CREATE_ROOM;
    }
}
