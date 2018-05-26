package ru.spbau.mit.game.client.gui;

import ru.spbau.mit.game.client.Game;
import ru.spbau.mit.game.client.ImagePack;
import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.requests.GetFieldPatchRequest;
import ru.spbau.mit.game.common.api.requests.GetFieldRequest;
import ru.spbau.mit.game.common.api.requests.GetRoomInfoRequest;
import ru.spbau.mit.game.common.api.requests.UpdateFieldRequest;
import ru.spbau.mit.game.common.api.response.GetFieldPatchResponse;
import ru.spbau.mit.game.common.api.response.GetFieldResponse;
import ru.spbau.mit.game.common.api.response.GetRoomInfoResponse;
import ru.spbau.mit.game.common.api.response.UpdateFieldResponse;
import ru.spbau.mit.game.common.api.units.Field;
import ru.spbau.mit.game.common.api.units.Field.Cell;
import ru.spbau.mit.game.common.api.units.Room;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

class GameFrame extends JFrame {
    private static final int TIMER_DELAY = 1000;
    private static final Font DEFAULT_FONT = new Font("Verdana", Font.BOLD, 14);

    private final JButton[][] buttons;
    private final JLabel currentPlayer;
    private final JLabel turnsCounter;
    private final Timer timer;
    private final Game game;
    private final Consumer<Game> callback;
    private final Game.Role role;
    private final ImagePack pack;
    private int fieldVersion = 0;

    GameFrame(Game game, ImagePack pack, Consumer<Game> callback) {
        super(game.room.name + ": " + game.room.host.name + " vs " + game.room.guest.name);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.game = game;
        if (game.room.host.id == game.player.id) {
            role = game.room.isHostStart ? Game.Role.X_PLAYER : Game.Role.O_PLAYER;
        } else if (game.room.guest.id == game.player.id) {
            role = !game.room.isHostStart ? Game.Role.X_PLAYER : Game.Role.O_PLAYER;
        } else {
            role = Game.Role.OBSERVER;
        }
        this.pack = pack;
        this.callback = callback;
        this.timer = new Timer(TIMER_DELAY, e -> {
            updateField();
            updateInfo();
            final Room.Status status = checkGameStatus();
            if (status != Room.Status.IN_PROGRESS) {
                gameFinished(status);
            }
        });

        final JPanel field = new JPanel(new GridLayout(game.size, game.size, 0, 0));
        buttons = new JButton[game.size][game.size];
        for (int i = 0; i < game.size; i++) {
            for (int j = 0; j < game.size; j++) {
                buttons[i][j] = createButton(i, j);
                field.add(buttons[i][j]);
            }
        }

        final JPanel infoPanel = new JPanel(new BorderLayout());
        currentPlayer = new JLabel();
        currentPlayer.setFont(DEFAULT_FONT);
        turnsCounter = new JLabel();
        turnsCounter.setFont(DEFAULT_FONT);
        infoPanel.add(currentPlayer, BorderLayout.WEST);
        infoPanel.add(turnsCounter, BorderLayout.EAST);

        add(field, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        initField();
        updateInfo();
        this.timer.start();
    }

    private JButton createButton(int row, int column) {
        final JButton button = new JButton();
        button.setBorder(null);
        button.addActionListener(e -> clicked(row, column));
        pack.initButton(button, role);
        return button;
    }

    private void clicked(int i, int j) {
        if (!isOurTern()) {
            showErrorMessage("It is not your turn!");
            return;
        }
        try {
            final UpdateFieldResponse request = (UpdateFieldResponse) API.request(
                    game.host, game.port,
                    new UpdateFieldRequest(
                            game.room.id,
                            game.password,
                            new Field.Diff(i, j, role.cell)
                    ));
            if (request.success) {
                updateField();
            } else {
                showErrorMessage("Invalid choice!");
            }
        } catch (IOException ignored) {
            connectionLost();
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initField() {
        try {
            final GetFieldResponse response = (GetFieldResponse) API.request(
                    game.host, game.port,
                    new GetFieldRequest(game.room.id));
            fieldVersion = response.field.iteration;
            final Cell[][] newCells = response.field.cells;
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons[0].length; j++) {
                    updateButton(i, j, newCells[i][j]);
                }
            }
        } catch (IOException ignored) {
            connectionLost();
        }
    }

    private Room.Status checkGameStatus() {
        try {
            final GetRoomInfoResponse response = (GetRoomInfoResponse) API.request(
                    game.host, game.port,
                    new GetRoomInfoRequest(game.room.id));
            return response.room.status;
        } catch (IOException ignored) {
            connectionLost();
        }
        return null;
    }

    private void updateInfo() {
        final boolean isHostActive = game.room.isHostStart ^ fieldVersion % 2 == 1;
        currentPlayer.setText("Active player: " + (isHostActive ? game.room.host.name : game.room.guest.name));
        turnsCounter.setText("Turn: " + (fieldVersion + 1));
    }

    private void updateField() {
        try {
            final GetFieldPatchResponse response = (GetFieldPatchResponse) API.request(
                    game.host, game.port,
                    new GetFieldPatchRequest(game.room.id, fieldVersion));
            final Field.Patch patch = response.patch;
            fieldVersion = patch.newVersion;
            for (Field.Diff diff : patch.diffs) {
                updateButton(diff.row, diff.column, diff.cell);
            }
        } catch (IOException ignored) {
            connectionLost();
        }
    }

    private void updateButton(int row, int column, Cell cell) {
        pack.updateButton(buttons[row][column], cell);
        buttons[row][column].setEnabled(cell == null);
    }

    private void connectionLost() {
        timer.stop();
        showErrorMessage("Connection lost!");
        callback.accept(game);
    }

    private void gameFinished(Room.Status status) {
        timer.stop();
        if (status == Room.Status.DRAW_GAME) {
            showInfoMessage("There is no winner in that game!");
            callback.accept(game);
        } else {
            final boolean isHostWin = (status == Room.Status.HOST_WIN);
            showInfoMessage((isHostWin ? game.room.host.name : game.room.guest.name) + " win the game!");
            callback.accept(game);
        }
    }

    private boolean isOurTern() {
        return role.ordinal() == fieldVersion % 2;
    }
}
