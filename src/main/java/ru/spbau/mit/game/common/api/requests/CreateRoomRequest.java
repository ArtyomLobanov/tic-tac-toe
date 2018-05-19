package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Player;

public class CreateRoomRequest implements Request {

    public final String roomName;
    public final Player owner;
    public final long password;

    public CreateRoomRequest(String roomName, Player owner, long password) {
        this.roomName = roomName;
        this.owner = owner;
        this.password = password;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.CREATE_ROOM;
    }
}
