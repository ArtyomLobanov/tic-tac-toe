package ru.spbau.mit.game.client.gui;

import ru.spbau.mit.game.client.Game;
import ru.spbau.mit.game.client.ImagePack;
import ru.spbau.mit.game.common.api.API;
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

public class MainFrame extends JFrame {
    private static final Font DEFAULT_FONT = new Font("Verdana", Font.BOLD, 14);

    private final Player player;
    private final long passwordHash;
    private final String host;
    private final int port;
    private final JPanel listPanel;

    private MainFrame(String host, int port, Player player, long passwordHash) {
        super("Tic-Tac-Toe game");
        final Dimension monitor = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(new Dimension(monitor.width / 2, monitor.height / 2));
        setResizable(false);
        setLocationRelativeTo(null);
        this.player = player;
        this.passwordHash = passwordHash;
        this.host = host;
        this.port = port;
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
        final JLabel nameLabel = new JLabel("Welcome, " + player.name + "!", SwingConstants.CENTER);
        nameLabel.setFont(DEFAULT_FONT);
        final JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 0));
        final JButton refresh = new JButton("Refresh");
        refresh.setFont(DEFAULT_FONT);
        refresh.addActionListener(e -> refreshRooms());
        buttons.add(refresh);
        final JButton create = new JButton("Create");
        create.setFont(DEFAULT_FONT);
        create.addActionListener(e -> refreshRooms());
        buttons.add(create);
        footer.add(nameLabel, BorderLayout.CENTER);
        footer.add(buttons, BorderLayout.EAST);
        return footer;
    }

    private JPanel createHeader() {
        final JPanel header = new JPanel(new GridLayout(1, 5, 5, 0));
        final JLabel nameLabel = new JLabel("Room name", SwingConstants.CENTER);
        nameLabel.setFont(DEFAULT_FONT);
        final JLabel hostLabel = new JLabel("Host name", SwingConstants.CENTER);
        hostLabel.setFont(DEFAULT_FONT);
        final JLabel guestLabel = new JLabel("Guest name", SwingConstants.CENTER);
        guestLabel.setFont(DEFAULT_FONT);
        final JLabel sizeLabel = new JLabel("Field size", SwingConstants.CENTER);
        sizeLabel.setFont(DEFAULT_FONT);
        final JLabel actionLabel = new JLabel("Action", SwingConstants.CENTER);
        actionLabel.setFont(DEFAULT_FONT);
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
        final JLabel sizeLabel = new JLabel(Integer.toString(room.size), SwingConstants.CENTER);
        sizeLabel.setFont(DEFAULT_FONT);
        panel.add(nameLabel);
        panel.add(hostLabel);
        panel.add(guestLabel);
        panel.add(sizeLabel);
        final JButton action;
        if (room.host.id == player.id) {
            action = new JButton("Remove");
            action.setEnabled(room.guest == null);
            action.addActionListener(e -> {
                try {
                    final DeleteRoomResponse response = (DeleteRoomResponse) API.request(
                            host, port,
                            new DeleteRoomRequest(room.id, passwordHash));
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
                            host, port,
                            new JoinRoomRequest(room.id, player.id, passwordHash));
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
            action = new JButton("Observe");
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
                host, port,
                new GetRoomInfoRequest(roomId));
        final Room room = roomResponse.room;
        final Game game = new Game(room, player, passwordHash, room.size, host, port);
        final GameFrame frame = new GameFrame(game, ImagePack.loadDefaultPack(50), e -> {
        });
        frame.setVisible(true);
    }

    private void refreshRooms() {
        try {
            final GetRoomsListResponse roomsList = (GetRoomsListResponse) API.request(
                    host, port,
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
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: host port name password");
            return;
        }
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final String name = args[2];
        final String password = args[3];
        final long passwordHash = password.hashCode() | ((long) (name.hashCode()) << 32);
        final RegisterPlayerResponse response = (RegisterPlayerResponse) API.request(host, port,
                new RegisterPlayerRequest(name, passwordHash));
        final Player player = new Player(name, response.id);
        SwingUtilities.invokeLater(() -> {
            final MainFrame game = new MainFrame(host, port, player, passwordHash);
            game.setVisible(true);
        });
    }
}
