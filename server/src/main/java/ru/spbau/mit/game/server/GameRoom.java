package ru.spbau.mit.game.server;

import ru.spbau.mit.game.common.api.units.Field;
import ru.spbau.mit.game.common.api.units.GameType;
import ru.spbau.mit.game.common.api.units.Player;
import ru.spbau.mit.game.common.api.units.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameRoom {
    public final long id;
    public final String name;
    public final Player host;
    public final GameType type;
    public final boolean isHostStart;

    private Player guest = null;
    private Room.Status status = Room.Status.WAIT_GUEST;

    public final ReadWriteLock fieldLock = new ReentrantReadWriteLock();
    public final Field.Cell[][] cells;
    public final List<Field.Diff> diffs = new ArrayList<>();

    public GameRoom(long id, String name, Player host, GameType type, boolean isHostStart) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.type = type;
        this.isHostStart = isHostStart;
        cells = new Field.Cell[type.fieldSize][type.fieldSize];
        for (int i = 0; i < type.fieldSize; i++) {
            for (int j = 0; j < type.fieldSize; j++) {
                cells[i][j] = null;
            }
        }
    }

    public Room getRoomInfo() {
        fieldLock.readLock().lock();
        Player guest = this.guest;
        Room.Status status = this.status;
        fieldLock.readLock().unlock();
        return new Room(name, id, host, guest, type, status, isHostStart);
    }

    public Field getField() {
        fieldLock.readLock().lock();
        Field.Cell[][] cells = new Field.Cell[type.fieldSize][type.fieldSize];
        for (int i = 0; i < type.fieldSize; i++) {
            System.arraycopy(this.cells[i], 0, cells[i], 0, type.fieldSize);
        }
        Player guest = this.guest;
        int iteration = diffs.size();
        fieldLock.readLock().unlock();

        return new Field(cells, host, guest, iteration);
    }

    public Field.Patch getFieldPatch(int fromVersion) {
        fieldLock.readLock().lock();
        int lastVersion = this.diffs.size();
        List<Field.Diff> diffs = this.diffs.subList(fromVersion, lastVersion);
        fieldLock.readLock().unlock();
        return new Field.Patch(fromVersion, lastVersion, diffs);
    }

    public boolean connectGuest(Player guest) {
        boolean success = false;
        fieldLock.writeLock().lock();
        if (this.guest == null) {
            this.guest = guest;
            this.status = Room.Status.IN_PROGRESS;
            success = true;
        }
        fieldLock.writeLock().unlock();
        return success;
    }

    public boolean processDiff(Field.Diff diff, long playerId) {
        boolean success = false;
        fieldLock.writeLock().lock();
        if (checkTurn(playerId, diff)) {
            diffs.add(diff);
            cells[diff.row][diff.column] = diff.cell;
            checkField(diff.row, diff.column);
            success = true;
        }
        fieldLock.writeLock().unlock();
        return success;
    }

    private boolean checkTurn(long playerId, Field.Diff diff) {
        final long expectedPlayer = isHostStart ^ diffs.size() % 2 == 1 ? host.id : guest.id;
        return status == Room.Status.IN_PROGRESS
                && playerId == expectedPlayer
                && diff.cell != null && diff.cell.ordinal() == diffs.size() % 2
                && cells[diff.row][diff.column] == null;
    }

    private void checkField(int x, int y) {
        if (diffs.size() == type.fieldSize * type.fieldSize) {
            status = Room.Status.DRAW_GAME;
            return;
        }
        for (Direction direction : Direction.values()) {
            int total = count(x, y, direction.dx, direction.dy) + count(x, y, -direction.dx, -direction.dy) - 1;
            if (total >= type.targetCount) {
                status = isHostStart ^ (diffs.size() % 2 == 0) ? Room.Status.HOST_WIN : Room.Status.GUEST_WIN;
                return;
            }
        }
    }

    private int count(int x, int y, int dx, int dy) {
        final Field.Cell target = cells[x][y];
        int count = 0;
        for (int i = 0; i < type.targetCount && inRange(x, y) && cells[x][y] == target; i++) {
            count++;
            x += dx;
            y += dy;
        }
        return count;
    }

    private boolean inRange(int x, int y) {
        return 0 <= x && x < cells.length && 0 <= y && y < cells[0].length;
    }

    private enum Direction {
        HORIZONTAL(1, 0),
        VERTICAL(0, 1),
        MAIN_DIAGONAL(1, 1),
        SIDE_DIAGONAL(1, -1);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
}
