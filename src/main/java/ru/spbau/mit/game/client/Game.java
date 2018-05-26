package ru.spbau.mit.game.client;

import ru.spbau.mit.game.common.api.ServerAddress;
import ru.spbau.mit.game.common.api.units.Field.Cell;
import ru.spbau.mit.game.common.api.units.Room;

public class Game {
    public final Room room;
    public final User user;
    public final ServerAddress address;

    public Game(Room room, User user, ServerAddress address) {
        this.room = room;
        this.user = user;
        this.address = address;
    }

    public enum Role {X_PLAYER(Cell.X), O_PLAYER(Cell.O), OBSERVER(null);
        public final Cell cell;

        Role(Cell cell) {
            this.cell = cell;
        }
    }
}
