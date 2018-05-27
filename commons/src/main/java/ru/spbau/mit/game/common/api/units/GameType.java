package ru.spbau.mit.game.common.api.units;

public enum GameType {
    CLASSIC_GAME("Classic", 3, 3),
    GOMOKU_GAME("Gomoku", 15, 5);
    public final String typeName;
    public final int fieldSize;
    public final int targetCount;

    GameType(String typeName, int fieldSize, int targetCount) {
        this.typeName = typeName;
        this.fieldSize = fieldSize;
        this.targetCount = targetCount;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
