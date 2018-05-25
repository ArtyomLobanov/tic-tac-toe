package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Field;

public class UpdateFieldRequest implements Request {
    public final long roomId;
    public final long password;
    public final int row;
    public final int column;
    public final Field.Cell cell;

    public UpdateFieldRequest(long roomId, long password, int row, int column, Field.Cell cell) {
        this.roomId = roomId;
        this.password = password;
        this.row = row;
        this.column = column;
        this.cell = cell;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.UPDATE_FIELD;
    }
}
