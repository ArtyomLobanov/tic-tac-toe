package ru.spbau.mit.game.client.gui;

import ru.spbau.mit.game.client.Game;
import ru.spbau.mit.game.client.ImagePack;
import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.requests.GetRoomInfoRequest;
import ru.spbau.mit.game.common.api.requests.GetRoomsListRequest;
import ru.spbau.mit.game.common.api.requests.JoinRoomRequest;
import ru.spbau.mit.game.common.api.requests.RegisterPlayerRequest;
import ru.spbau.mit.game.common.api.response.GetRoomInfoResponse;
import ru.spbau.mit.game.common.api.response.GetRoomsListResponse;
import ru.spbau.mit.game.common.api.response.JoinRoomResponse;
import ru.spbau.mit.game.common.api.response.RegisterPlayerResponse;
import ru.spbau.mit.game.common.api.units.Player;
import ru.spbau.mit.game.common.api.units.Room;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class MainFrame extends JFrame {
    private static final Font DEFAULT_FONT = new Font("Verdana", Font.BOLD, 14);

    private final Player player;
    private final long passwordHash;
    private final String host;
    private final int port;
    private final JPanel listPanel;

    private MainFrame(String host, int port, Player player, long passwordHash) {
        super("Tic-Tac-Toe game");
        setMinimumSize(new Dimension(700, 200));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        this.player = player;
        this.passwordHash = passwordHash;
        this.host = host;
        this.port = port;
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        final JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(listPanel, BorderLayout.NORTH);
        add(new JScrollPane(wrapper), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        refreshRooms();
    }

    private JPanel createFooter() {
        final JPanel footer = new JPanel(new BorderLayout());
        final JLabel nameLabel = new JLabel("Your name:" + player.name);
        nameLabel.setFont(DEFAULT_FONT);
        final JButton refresh = new JButton("Refresh");
        refresh.setFont(DEFAULT_FONT);
        refresh.addActionListener(e -> refreshRooms());
        footer.add(nameLabel, BorderLayout.WEST);
        footer.add(refresh, BorderLayout.EAST);
        return footer;
    }

    private JPanel createRoomPanel(Room room) {
        final JPanel panel = new JPanel(new GridLayout(1, 5, 10, 0));
        final JLabel nameLabel = new JLabel("Room: " + room.name);
        nameLabel.setFont(DEFAULT_FONT);
        final JLabel hostLabel = new JLabel("Host: " + room.host.name);
        hostLabel.setFont(DEFAULT_FONT);
        final JLabel guestLabel = new JLabel("Guest: " + Optional.ofNullable(room.guest)
                .map(p -> p.name).orElse("None"));
        guestLabel.setFont(DEFAULT_FONT);
        final JLabel sizeLabel = new JLabel("Field size: " + room.size);
        sizeLabel.setFont(DEFAULT_FONT);
        panel.add(nameLabel);
        panel.add(hostLabel);
        panel.add(guestLabel);
        panel.add(sizeLabel);
        if (room.guest == null) {
            final JButton join = new JButton("Join");
            join.setFont(DEFAULT_FONT);
            join.addActionListener(e -> {
                try {
                    final JoinRoomResponse response = (JoinRoomResponse) API.request(
                            host, port,
                            new JoinRoomRequest(room.id, player.id, passwordHash));
                    if (response.success) {
                        openGame(room.id);
                    } else {
                        showErrorMessage("Can't join room.");
                        refreshRooms();
                    }
                } catch (IOException ignored) {
                    connectionLost();
                }
            });
            panel.add(join);
        } else {
            final JButton join = new JButton("Observe");
            join.setFont(DEFAULT_FONT);
            join.addActionListener(e -> {
                try {
                    openGame(room.id);
                } catch (IOException ignored) {
                    connectionLost();
                }
            });
            panel.add(join);
        }
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
