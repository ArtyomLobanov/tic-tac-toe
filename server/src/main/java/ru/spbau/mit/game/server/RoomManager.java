package ru.spbau.mit.game.server;

import ru.spbau.mit.game.common.api.units.GameType;
import ru.spbau.mit.game.common.api.units.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RoomManager {
    private final AtomicLong FREE_ID = new AtomicLong(1);
    private final Map<Long, GameRoom> id2room = new ConcurrentHashMap<>();
    private final ReadWriteLock listLock = new ReentrantReadWriteLock();
    private LinkedList<GameRoom> roomList = new LinkedList<>();

    public long createRoom(Player host, String name, GameType type, boolean isHostStarts) {
        long id = FREE_ID.getAndIncrement();
        GameRoom gameRoom = new GameRoom(id, name, host, type, isHostStarts);
        id2room.put(id, gameRoom);
        listLock.writeLock().lock();
        roomList.addFirst(gameRoom);
        listLock.writeLock().unlock();
        return id;
    }

    public boolean deleteRoom(long id, long userId) {
        GameRoom room = id2room.get(id);
        if (room != null && room.host.id == userId) {
            room = id2room.remove(id);
            return room != null;
        }
        return false;
    }

    public boolean joinRoom(Player guest, long roomId) {
        GameRoom room = id2room.get(roomId);
        return room != null && room.host.id != guest.id && room.connectGuest(guest);
    }

    public GameRoom getRoom(long id) {
        return id2room.get(id);
    }

    public int getRoomCount() {
        return roomList.size();
    }

    public List<GameRoom> getRooms(int startPosition, int limit) {
        List<GameRoom> result = new ArrayList<>();
        listLock.readLock().lock();
        if (startPosition < roomList.size()) {
            result = roomList.subList(startPosition, Math.min(startPosition + limit, roomList.size()));
        }
        listLock.readLock().unlock();
        return result;
    }

    public void refreshRoomsList() {
        listLock.writeLock().lock();
        roomList = roomList.stream().filter(gr -> id2room.containsKey(gr.id))
                .collect(Collectors.toCollection(LinkedList::new));
        listLock.writeLock().unlock();
    }
}
