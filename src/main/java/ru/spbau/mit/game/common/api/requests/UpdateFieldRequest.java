package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;

public class UpdateFieldRequest implements Request {
    public final long roomId;
    public final long password;
    public final int row;
    public final int column;

    public UpdateFieldRequest(long roomId, long password, int row, int column) {
        this.roomId = roomId;
        this.password = password;
        this.row = row;
        this.column = column;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.UPDATE_FIELD;
    }
}
