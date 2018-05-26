package ru.spbau.mit.game.common.api.requests;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Field;

public class UpdateFieldRequest implements Request {
    public final long roomId;
    public final long password;
    public final Field.Diff diff;

    public UpdateFieldRequest(long roomId, long password, Field.Diff diff) {
        this.roomId = roomId;
        this.password = password;
        this.diff = diff;
    }

    @Override
    public API.Type getDialogType() {
        return API.Type.UPDATE_FIELD;
    }
}
