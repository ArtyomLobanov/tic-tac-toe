package ru.spbau.mit.game.common.api.units;

import java.util.List;

public class Field {
    public final Cell[][] cells;
    public final Player firstPlayerId;
    public final Player secondPlayerId;
    public final int iteration;

    public Field(Cell[][] cells, Player firstPlayerId, Player secondPlayerId, int iteration) {
        this.cells = cells;
        this.firstPlayerId = firstPlayerId;
        this.secondPlayerId = secondPlayerId;
        this.iteration = iteration;
    }

    public enum Cell {X, O}

    public static class Diff {
        public final int row;
        public final int column;
        public final Cell cell;

        public Diff(int row, int column, Cell cell) {
            this.row = row;
            this.column = column;
            this.cell = cell;
        }
    }

    public static class Patch {
        public final int oldVersion;
        public final int newVersion;
        public final List<Diff> diffs;

        public Patch(int oldVersion, int newVersion, List<Diff> diffs) {
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
            this.diffs = diffs;
        }
    }
}
