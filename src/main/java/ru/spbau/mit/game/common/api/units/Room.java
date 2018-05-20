package ru.spbau.mit.game.common.api.units;

public class Room {
    public enum Status{HOST_WIN, GUEST_WIN, DRAW_GAME, IN_PROGRESS, WAIT_GUEST}

    public String name;
    public long id;
    public Player host;
    public Player guest;
    public Status status;
}
