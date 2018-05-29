package ru.spbau.mit.game.server;

import ru.spbau.mit.game.common.api.units.Player;
import ru.spbau.mit.game.server.exception.BrokenAuthTokenException;
import ru.spbau.mit.game.server.units.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PlayerManager {
    //Max AFK time
    public final static long SESSION_TTL = TimeUnit.MINUTES.toMillis(15);
    private final static Random RANDOM_GENERATOR = new Random();
    private final static AtomicLong FREE_ID = new AtomicLong(1);

    private ConcurrentHashMap<String, User> name2User = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, User> id2User = new ConcurrentHashMap<>();

    private ReadWriteLock authTokenLock = new ReentrantReadWriteLock();
    private HashMap<Long, Long> userId2authToken = new HashMap<>();
    private HashMap<Long, Long> authToken2UserId = new HashMap<>();

    private ConcurrentHashMap<Long, Long> authToken2Time = new ConcurrentHashMap<>();

    public long getUserId(String userName) {
        return name2User.getOrDefault(userName, new User(new Player("", -1), "")).player.id;
    }

    public long getOrCreateUserId(String userName, String password) {
        User user = name2User.computeIfAbsent(userName,
                s -> new User(new Player(userName, FREE_ID.getAndIncrement()), password));
        id2User.put(user.player.id, user);
        return user.player.id;
    }

    public long getAuthToken(long userId, String userPassword) {
        User user = id2User.get(userId);
        if (user != null && user.passwordHash == User.hash(userPassword)) {
            authTokenLock.readLock().lock();
            long authToken = userId2authToken.getOrDefault(userId, -1L);
            if (authToken > 0) {
                authToken2Time.put(authToken, System.currentTimeMillis());
            }
            authTokenLock.readLock().unlock();
            if (authToken == -1) {
                authTokenLock.writeLock().lock();
                authToken = userId2authToken.computeIfAbsent(userId, id -> Math.abs(RANDOM_GENERATOR.nextLong()));
                authToken2UserId.put(authToken, userId);
                authToken2Time.put(authToken, System.currentTimeMillis());
                authTokenLock.writeLock().unlock();
                System.out.println("Create token: " + authToken);
            }
            return authToken;
        }
        return -1;
    }

    public long getUserIdByToken(long authToken) {
        if (authToken < 0) {
            throw new BrokenAuthTokenException();
        }
        authTokenLock.readLock().lock();
        long userId = authToken2UserId.getOrDefault(authToken, -1L);
        if (userId > 0) {
            authToken2Time.put(authToken, System.currentTimeMillis());
        }
        authTokenLock.readLock().unlock();
        if (userId < 0) {
            throw new BrokenAuthTokenException();
        }
        return userId;
    }

    public Player getPlayerById(long userId) {
        return id2User.getOrDefault(userId, new User(new Player("", -1), "")).player;
    }

    public void checkSessions() {
        authTokenLock.writeLock().lock();
        long currentTime = System.currentTimeMillis();
        Set<Long> tokens = authToken2Time.keySet();
        for (Long token : tokens) {
            if (authToken2Time.compute(token, (tok, time) -> currentTime - time) > SESSION_TTL) {
                System.out.println("Pending remove token: " + token);
                authToken2Time.remove(token);
                if (authToken2UserId.containsKey(token)) {
                    long userId = authToken2UserId.remove(token);
                    userId2authToken.remove(userId);
                }
            }
        }
        authTokenLock.writeLock().unlock();
    }
}
