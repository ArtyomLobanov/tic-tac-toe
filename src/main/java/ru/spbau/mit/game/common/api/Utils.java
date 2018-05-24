package ru.spbau.mit.game.common.api;

import java.io.IOException;
import java.io.Reader;

class Utils {
    static String readLine(Reader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();
        int symbol = reader.read();
        while (symbol != -1 && symbol != '\n') {
            builder.append((char) symbol);
            symbol = reader.read();
        }
        return builder.toString().trim();
    }
}
