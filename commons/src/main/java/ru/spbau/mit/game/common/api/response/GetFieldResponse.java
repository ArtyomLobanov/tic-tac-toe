package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Field;

public class GetFieldResponse implements Response {
    public final Field field;

    public GetFieldResponse(Field field) {
        this.field = field;
    }

    @Override
    public API.Type getType() {
        return API.Type.GET_FIELD;
    }
}
