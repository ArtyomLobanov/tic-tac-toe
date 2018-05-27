package ru.spbau.mit.game.server.units;

import ru.spbau.mit.game.common.api.units.Player;

public class User {
    public final Player player;
    public final long passwordHash;

    public User(Player player, String password) {
        this.player = player;
        this.passwordHash = hash(password);
    }

    public static long hash(String password) {
        long hash = 0;
        for (char c : password.toCharArray()) {
            hash = 177 * hash + c;
        }
        return hash;
    }
}
