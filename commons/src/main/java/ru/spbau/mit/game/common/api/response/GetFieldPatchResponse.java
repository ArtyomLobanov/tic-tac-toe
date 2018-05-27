package ru.spbau.mit.game.common.api.response;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.units.Field;

public class GetFieldPatchResponse implements Response {
    public final Field.Patch patch;

    public GetFieldPatchResponse(Field.Patch patch) {
        this.patch = patch;
    }

    @Override
    public API.Type getType() {
        return API.Type.GET_FIELD_PATCH;
    }
}
