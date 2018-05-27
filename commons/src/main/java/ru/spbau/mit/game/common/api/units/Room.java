package ru.spbau.mit.game.common.api.units;

public class Room {

    public enum Status {HOST_WIN, GUEST_WIN, DRAW_GAME, IN_PROGRESS, WAIT_GUEST}

    public final String name;
    public final long id;
    public final Player host;
    public final Player guest;
    public final GameType type;
    public final Status status;
    public final boolean isHostStart;

    public Room(String name, long id, Player host, Player guest, GameType type, Status status, boolean isHostStart) {
        this.name = name;
        this.id = id;
        this.host = host;
        this.guest = guest;
        this.type = type;
        this.status = status;
        this.isHostStart = isHostStart;
    }
}
