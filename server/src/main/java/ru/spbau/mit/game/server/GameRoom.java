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
        if (status == Room.Status.IN_PROGRESS && checkTurn(playerId == host.id, diff.cell)) {
            diffs.add(diff);
            cells[diff.row][diff.column] = diff.cell;
            success = true;
        }
        checkField(diff.row, diff.column);
        fieldLock.writeLock().unlock();
        return success;
    }

    private boolean checkTurn(boolean hostDiff, Field.Cell cell) {
        return ((isHostStart && ((hostDiff && diffs.size() % 2 == 0) || (!hostDiff && diffs.size() % 2 == 1))) ||
                (!isHostStart && ((!hostDiff && diffs.size() % 2 == 0) || (hostDiff && diffs.size() % 2 == 1)))) &&
                ((diffs.size() % 2 == 0 && cell == Field.Cell.X) || (diffs.size() % 2 != 0 && cell == Field.Cell.O));
    }

    private void checkField(int x, int y) {
        if (diffs.size() == type.fieldSize * type.fieldSize) {
            status = Room.Status.DRAW_GAME;
        }
        Field.Cell target = cells[x][y];
        boolean win = false;
        if (x >= type.targetCount) {
            boolean tmp = true;
            for (int j = 1; j < type.targetCount; j++) {
                if (cells[x - j][y] != target) {
                    tmp = false;
                    break;
                }
            }
            win = win || tmp;
            tmp = true;
            if (y >= type.targetCount) {
                for (int j = 1; j < type.targetCount; j++) {
                    if (cells[x - j][y - j] != target) {
                        tmp = false;
                        break;
                    }
                }
            }
            win = win || tmp;
            tmp = true;
            if (y <= type.fieldSize - type.targetCount) {
                for (int j = 1; j < type.targetCount; j++) {
                    if (cells[x - j][y + j] != target) {
                        tmp = false;
                        break;
                    }
                }
            }
            win = win || tmp;
        }
        if (x <= type.fieldSize - type.targetCount) {
            boolean tmp = true;
            for (int j = 1; j < type.targetCount; j++) {
                if (cells[x + j][y] != target) {
                    tmp = false;
                    break;
                }
            }
            win = win || tmp;
            tmp = true;
            if (y >= type.targetCount) {
                for (int j = 1; j < type.targetCount; j++) {
                    if (cells[x + j][y - j] != target) {
                        tmp = false;
                        break;
                    }
                }
            }
            win = win || tmp;
            tmp = true;
            if (y <= type.fieldSize - type.targetCount) {
                for (int j = 1; j < type.targetCount; j++) {
                    if (cells[x + j][y + j] != target) {
                        tmp = false;
                        break;
                    }
                }
            }
            win = win || tmp;
        }
        if (y >= type.targetCount) {
            boolean tmp = true;
            for (int j = 1; j < type.targetCount; j++) {
                if (cells[x][y - j] != target) {
                    tmp = false;
                    break;
                }
            }
            win = win || tmp;
        }
        if (y <= type.fieldSize - type.targetCount) {
            boolean tmp = true;
            for (int j = 1; j < type.targetCount; j++) {
                if (cells[x][y + j] != target) {
                    tmp = false;
                    break;
                }
            }
            win = win || tmp;
        }
        if (win) {
            status = isHostStart^(diffs.size() % 2 == 1) ? Room.Status.HOST_WIN : Room.Status.GUEST_WIN;
        }
    }
}
