package ru.spbau.mit.game.common.api.units;

public enum GameType {
    CLASSIC_GAME("Classic", 3),
    GOMOKU_GAME("Gomoku", 15);
    public final String typeName;
    public final int fieldSize;

    GameType(String typeName, int fieldSize) {
        this.typeName = typeName;
        this.fieldSize = fieldSize;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
