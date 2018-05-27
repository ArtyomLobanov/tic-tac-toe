package ru.spbau.mit.game.client.gui;

import ru.spbau.mit.game.common.api.ServerAddress;
import ru.spbau.mit.game.common.api.units.GameType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import static ru.spbau.mit.game.client.Constants.DEFAULT_FONT;

class Dialogs {
    static Optional<GameSettings> createUser(Component relativeTo) {
        final JDialog dialog = new JDialog();

        final JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(10, 10, 5, 10));

        final JPanel footer = new JPanel(new FlowLayout());
        final JButton okButton = new JButton("OK");
        okButton.setFont(DEFAULT_FONT);
        okButton.addActionListener(e -> dialog.dispose());
        footer.add(okButton);
        content.add(footer, BorderLayout.SOUTH);

        final JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 10));
        final JLabel hostLabel = new JLabel("Server host:");
        hostLabel.setFont(DEFAULT_FONT);
        final JTextField hostInput = new JTextField();
        hostInput.setFont(DEFAULT_FONT);
        final JLabel portLabel = new JLabel("Server port:");
        portLabel.setFont(DEFAULT_FONT);
        final JTextField portInput = new JTextField();
        portInput.setFont(DEFAULT_FONT);
        final JLabel nameLabel = new JLabel("Your name:");
        nameLabel.setFont(DEFAULT_FONT);
        final JTextField nameInput = new JTextField();
        nameInput.setFont(DEFAULT_FONT);
        final JLabel passwordLabel = new JLabel("Your password:");
        passwordLabel.setFont(DEFAULT_FONT);
        final JTextField passwordInput = new JTextField();
        passwordInput.setFont(DEFAULT_FONT);
        inputPanel.add(hostLabel);
        inputPanel.add(hostInput);
        inputPanel.add(portLabel);
        inputPanel.add(portInput);
        inputPanel.add(nameLabel);
        inputPanel.add(nameInput);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordInput);
        content.add(inputPanel, BorderLayout.NORTH);

        final Validator<GameSettings> validator = new Validator<>(okButton,
                () -> new GameSettings(
                        new ServerAddress(hostInput.getText(), Integer.parseInt(portInput.getText())),
                        nameInput.getText(),
                        passwordInput.getText()),
                () -> !hostInput.getText().isEmpty(),
                () -> portInput.getText().matches("\\d{1,4}") && Integer.parseInt(portInput.getText()) < (1 << 16),
                () -> nameInput.getText().matches("[a-zA-Z0-9]+"),
                () -> !passwordInput.getText().isEmpty());
        okButton.addActionListener(validator);
        nameInput.getDocument().addDocumentListener(validator);
        passwordInput.getDocument().addDocumentListener(validator);
        validator.checkState();

        dialog.setContentPane(content);
        dialog.setTitle("User creation");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(relativeTo);
        dialog.setModal(true);
        dialog.setVisible(true);

        return validator.getValue();
    }

    static Optional<RoomSettings> createRoom(Component relativeTo) {
        final JDialog dialog = new JDialog();

        final JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(10, 10, 5, 10));

        final JPanel footer = new JPanel(new FlowLayout());
        final JButton okButton = new JButton("OK");
        okButton.setFont(DEFAULT_FONT);
        okButton.addActionListener(e -> dialog.dispose());
        footer.add(okButton);
        content.add(footer, BorderLayout.SOUTH);

        final JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 10));
        final JLabel nameLabel = new JLabel("Room name:");
        nameLabel.setFont(DEFAULT_FONT);
        final JTextField nameInput = new JTextField();
        nameInput.setFont(DEFAULT_FONT);
        final JLabel gameLabel = new JLabel("Game type:");
        gameLabel.setFont(DEFAULT_FONT);
        final JComboBox<GameType> gameInput = new JComboBox<>(GameType.values());
        gameInput.setFont(DEFAULT_FONT);
        gameInput.setEditable(false);
        gameInput.setSelectedIndex(0);
        inputPanel.add(nameLabel);
        inputPanel.add(nameInput);
        inputPanel.add(gameLabel);
        inputPanel.add(gameInput);
        content.add(inputPanel, BorderLayout.NORTH);

        final Validator<RoomSettings> validator = new Validator<>(okButton,
                () -> new RoomSettings(nameInput.getText(), (GameType) gameInput.getSelectedItem()),
                () -> !nameInput.getText().isEmpty());
        okButton.addActionListener(validator);
        nameInput.getDocument().addDocumentListener(validator);
        validator.checkState();

        dialog.setContentPane(content);
        dialog.setTitle("Room creation");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(relativeTo);
        dialog.setModal(true);
        dialog.setVisible(true);

        return validator.getValue();
    }

    private static class Validator<T> implements DocumentListener, ActionListener {

        private final Supplier<Boolean>[] checkers;
        private final JButton targetButton;
        private final Supplier<T> resultCreator;
        private T value;

        @SafeVarargs
        private Validator(JButton targetButton, Supplier<T> resultCreator, Supplier<Boolean>... checkers) {
            this.targetButton = targetButton;
            this.resultCreator = resultCreator;
            this.checkers = checkers;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkState();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkState();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkState();
        }

        private void checkState() {
            final boolean state = Arrays.stream(checkers)
                    .allMatch(Supplier::get);
            targetButton.setEnabled(state);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == targetButton) {
                value = resultCreator.get();
            }
        }

        Optional<T> getValue() {
            return Optional.ofNullable(value);
        }
    }

    static class GameSettings {
        final ServerAddress address;
        final String name;
        final String password;

        private GameSettings(ServerAddress address, String name, String password) {
            this.address = address;
            this.name = name;
            this.password = password;
        }
    }

    static class RoomSettings {
        final String name;
        final GameType type;

        private RoomSettings(String name, GameType type) {
            this.name = name;
            this.type = type;
        }
    }
}
