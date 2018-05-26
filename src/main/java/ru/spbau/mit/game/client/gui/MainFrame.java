package ru.spbau.mit.game.client.gui;

import ru.spbau.mit.game.client.Game;
import ru.spbau.mit.game.client.ImagePack;
import ru.spbau.mit.game.client.User;
import ru.spbau.mit.game.client.gui.Dialogs.GameSettings;
import ru.spbau.mit.game.client.gui.Dialogs.RoomSettings;
import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.ServerAddress;
import ru.spbau.mit.game.common.api.requests.*;
import ru.spbau.mit.game.common.api.response.*;
import ru.spbau.mit.game.common.api.units.Player;
import ru.spbau.mit.game.common.api.units.Room;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static ru.spbau.mit.game.client.Constants.BOLD_FONT;
import static ru.spbau.mit.game.client.Constants.DEFAULT_FONT;

public class MainFrame extends JFrame {
    private final User user;
    private final ServerAddress address;
    private final JPanel listPanel;

    private MainFrame(ServerAddress address, User user) {
        super("Tic-Tac-Toe game");
        final Dimension monitor = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(new Dimension(monitor.width / 2, monitor.height / 2));
        setResizable(false);
        setLocationRelativeTo(null);
        this.user = user;
        this.address = address;
        listPanel = new JPanel();
        final JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(listPanel, BorderLayout.NORTH);
        add(new JScrollPane(wrapper, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        add(createHeader(), BorderLayout.NORTH);
        refreshRooms();
    }

    private JPanel createFooter() {
        final JPanel footer = new JPanel(new BorderLayout());
        final JLabel nameLabel = new JLabel("Welcome, " + user.player.name + "!", SwingConstants.CENTER);
        nameLabel.setFont(DEFAULT_FONT);
        final JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 0));
        final JButton refresh = new JButton("Refresh");
        refresh.setFont(DEFAULT_FONT);
        refresh.addActionListener(e -> refreshRooms());
        buttons.add(refresh);
        final JButton create = new JButton("Create");
        create.setFont(DEFAULT_FONT);
        create.addActionListener(e -> createRoom());
        buttons.add(create);
        footer.add(nameLabel, BorderLayout.CENTER);
        footer.add(buttons, BorderLayout.EAST);
        return footer;
    }

    private JPanel createHeader() {
        final JPanel header = new JPanel(new GridLayout(1, 5, 5, 0));
        final JLabel nameLabel = new JLabel("Room name", SwingConstants.CENTER);
        nameLabel.setFont(BOLD_FONT);
        final JLabel hostLabel = new JLabel("Host name", SwingConstants.CENTER);
        hostLabel.setFont(BOLD_FONT);
        final JLabel guestLabel = new JLabel("Guest name", SwingConstants.CENTER);
        guestLabel.setFont(BOLD_FONT);
        final JLabel sizeLabel = new JLabel("Game type", SwingConstants.CENTER);
        sizeLabel.setFont(BOLD_FONT);
        final JLabel actionLabel = new JLabel("Action", SwingConstants.CENTER);
        actionLabel.setFont(BOLD_FONT);
        header.add(nameLabel);
        header.add(hostLabel);
        header.add(guestLabel);
        header.add(sizeLabel);
        header.add(actionLabel);
        return header;
    }

    private JPanel createRoomPanel(Room room) {
        final JPanel panel = new JPanel(new GridLayout(1, 5, 5, 0));
        final JLabel nameLabel = new JLabel(room.name, SwingConstants.CENTER);
        nameLabel.setFont(DEFAULT_FONT);
        final JLabel hostLabel = new JLabel(room.host.name, SwingConstants.CENTER);
        hostLabel.setFont(DEFAULT_FONT);
        final JLabel guestLabel = new JLabel(Optional.ofNullable(room.guest)
                .map(p -> p.name).orElse("-"), SwingConstants.CENTER);
        guestLabel.setFont(DEFAULT_FONT);
        final JLabel sizeLabel = new JLabel(room.type.typeName, SwingConstants.CENTER);
        sizeLabel.setFont(DEFAULT_FONT);
        panel.add(nameLabel);
        panel.add(hostLabel);
        panel.add(guestLabel);
        panel.add(sizeLabel);
        final JButton action;
        if (room.host.id == user.player.id && room.guest == null) {
            action = new JButton("Remove");
            action.addActionListener(e -> {
                try {
                    final DeleteRoomResponse response = (DeleteRoomResponse) API.request(
                            address,
                            new DeleteRoomRequest(room.id, user.passwordHash));
                    if (!response.success) {
                        showErrorMessage("Can't remove room.");
                    }
                    refreshRooms();
                } catch (IOException ignored) {
                    connectionLost();
                }
            });
        } else if (room.guest == null) {
            action = new JButton("Join");
            action.addActionListener(e -> {
                try {
                    final JoinRoomResponse response = (JoinRoomResponse) API.request(
                            address,
                            new JoinRoomRequest(room.id,user.player.id, user.passwordHash));
                    if (response.success) {
                        openGame(room.id);
                    } else {
                        showErrorMessage("Can't join room.");
                    }
                    refreshRooms();
                } catch (IOException ignored) {
                    connectionLost();
                }
            });
        } else {
            action = new JButton("Open");
            action.addActionListener(e -> {
                try {
                    openGame(room.id);
                } catch (IOException ignored) {
                    connectionLost();
                }
            });
        }
        action.setFont(DEFAULT_FONT);
        panel.add(action);
        final int preferredHeight = panel.getPreferredSize().height;
        panel.setPreferredSize(new Dimension(getWidth(), preferredHeight));
        return panel;
    }

    private void openGame(long roomId) throws IOException {
        final GetRoomInfoResponse roomResponse = (GetRoomInfoResponse) API.request(
                address,
                new GetRoomInfoRequest(roomId));
        final Room room = roomResponse.room;
        final Game game = new Game(room, user, address);
        //todo icon sizes
        final GameFrame frame = new GameFrame(game, ImagePack.loadDefaultPack(50), e -> {});
        frame.setVisible(true);
    }

    private void createRoom() {
        final Optional<RoomSettings> result = Dialogs.createRoom();
        if (!result.isPresent()) {
            return;
        }
        final RoomSettings settings = result.get();
        try {
            final CreateRoomResponse response = (CreateRoomResponse) API.request(
                    address,
                    new CreateRoomRequest(settings.name, settings.type, user.player, user.passwordHash));
            if (!response.success) {
                showErrorMessage("Unable to create room!");
            }
            refreshRooms();
        } catch (IOException ignored) {
            connectionLost();
        }
    }

    private void refreshRooms() {
        try {
            final GetRoomsListResponse roomsList = (GetRoomsListResponse) API.request(
                    address,
                    new GetRoomsListRequest());
            listPanel.removeAll();
            listPanel.setLayout(new GridLayout(roomsList.rooms.size() + 1, 1, 0, 10));
            for (Room room : roomsList.rooms) {
                listPanel.add(createRoomPanel(room));
            }
        } catch (IOException ignored) {
            connectionLost();
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void connectionLost() {
        showErrorMessage("Connection lost!");
        setVisible(false);
        dispose();
    }

    public static void main(String[] args) throws IOException {
        final Optional<GameSettings> result = Dialogs.createUser();
        if (!result.isPresent()) {
            return;
        }
        final GameSettings settings = result.get();
        final RegisterPlayerResponse response = (RegisterPlayerResponse) API.request(settings.address,
                new RegisterPlayerRequest(settings.name, User.hash(settings.password)));
        final Player player = new Player(settings.name, response.id);
        final User user = new User(player, settings.password);
        SwingUtilities.invokeLater(() -> {
            final MainFrame game = new MainFrame(settings.address, user);
            game.setVisible(true);
        });
    }
}
