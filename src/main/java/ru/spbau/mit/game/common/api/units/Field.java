package ru.spbau.mit.game.common.api.units;

public class Field {
    public enum Cell {X, O}
    public final Cell[][] field;
    public final Player firstPlayerId;
    public final Player secondPlayerId;
    public final int iteration;

    public Field(Cell[][] field, Player firstPlayerId, Player secondPlayerId, int iteration) {
        this.field = field;
        this.firstPlayerId = firstPlayerId;
        this.secondPlayerId = secondPlayerId;
        this.iteration = iteration;
    }
}
