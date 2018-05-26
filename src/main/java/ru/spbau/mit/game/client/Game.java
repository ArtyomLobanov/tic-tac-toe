package ru.spbau.mit.game.client;

import ru.spbau.mit.game.common.api.units.Field.Cell;
import ru.spbau.mit.game.common.api.units.Player;
import ru.spbau.mit.game.common.api.units.Room;

public class Game {
    public final Room room;
    public final Player player;
    public final long password;
    public final int size;
    public final String host;
    public final int port;

    public Game(Room room, Player player, long password, int size, String host, int port) {
        this.room = room;
        this.player = player;
        this.password = password;
        this.size = size;
        this.host = host;
        this.port = port;
    }

    public enum GameResult {WIN, LOSE, DRAW, CONNECTION_LOST}
    public enum Role {X_PLAYER(Cell.X), O_PLAYER(Cell.O), OBSERVER(null);
        public final Cell cell;

        Role(Cell cell) {
            this.cell = cell;
        }
    }
}
