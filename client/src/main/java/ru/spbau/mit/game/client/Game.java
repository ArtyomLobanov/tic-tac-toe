package ru.spbau.mit.game.client;

import ru.spbau.mit.game.common.api.ServerAddress;
import ru.spbau.mit.game.common.api.units.Field.Cell;
import ru.spbau.mit.game.common.api.units.Player;
import ru.spbau.mit.game.common.api.units.Room;

public class Game {
    public final Room room;
    public final Player player;
    public final ServerAddress address;
    public final long authToken;

    public Game(Room room, Player player, ServerAddress address, long authToken) {
        this.room = room;
        this.player = player;
        this.address = address;
        this.authToken = authToken;
    }

    public enum Role {X_PLAYER(Cell.X), O_PLAYER(Cell.O), OBSERVER(null);
        public final Cell cell;

        Role(Cell cell) {
            this.cell = cell;
        }
    }
}
