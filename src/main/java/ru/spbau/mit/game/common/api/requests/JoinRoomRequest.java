package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class JoinRoomRequest implements Request {
    public final long roomId;
    public final long playerId;
    public final long playerPassword;

    public JoinRoomRequest(long roomId, long playerId, long playerPassword) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.playerPassword = playerPassword;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.JOIN_ROOM;
    }
}
