package ru.spbau.mit.game.server;

import ru.spbau.mit.game.common.api.requests.*;
import ru.spbau.mit.game.common.api.response.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Server extends AbstractConnectionPool {
    private final static Server INSTANCE = new Server();
    private final PlayerManager playerManager = new PlayerManager();
    private final RoomManager roomManager = new RoomManager();
    private final Thread sessionChecker;

    public Server() {
        sessionChecker = new Thread(() -> {
            try {
                while (true) {
                    TimeUnit.MILLISECONDS.sleep(PlayerManager.SESSION_TTL);
                    playerManager.checkSessions();
                    roomManager.refreshRoomsList();
                }
            } catch (InterruptedException e) {
                //TODO log
            }
        });
        sessionChecker.start();
    }

    @Override
    protected Response processRequest(Request request) {
        switch (request.getDialogType()) {
            case CREATE_ROOM: {
                CreateRoomRequest createRoomRequest = (CreateRoomRequest)request;
                return new CreateRoomResponse(roomManager.createRoom(playerManager.getPlayerById(
                        playerManager.getUserIdByToken(createRoomRequest.authToken)),
                        createRoomRequest.roomName, createRoomRequest.type, createRoomRequest.hostStarts));
            }
            case DELETE_ROOM: {
                DeleteRoomRequest deleteRoomRequest = (DeleteRoomRequest)request;
                return new DeleteRoomResponse(roomManager.deleteRoom(deleteRoomRequest.roomId,
                        playerManager.getUserIdByToken(deleteRoomRequest.authToken)));
            }
            case GET_FIELD: {
                GetFieldRequest getFieldRequest = (GetFieldRequest)request;
                GameRoom room = roomManager.getRoom(getFieldRequest.roomId);
                if (room == null) {
                    return new GetRoomInfoResponse(null);
                }
                return new GetFieldResponse(roomManager.getRoom(getFieldRequest.roomId).getField());
            }
            case GET_FIELD_PATCH: {
                GetFieldPatchRequest getFieldPatchRequest = (GetFieldPatchRequest)request;
                GameRoom room = roomManager.getRoom(getFieldPatchRequest.roomId);
                if (room == null) {
                    return new GetRoomInfoResponse(null);
                }
                return new GetFieldPatchResponse(room.getFieldPatch(getFieldPatchRequest.startVersion));
            }
            case GET_ROOM_INFO: {
                GetRoomInfoRequest getRoomInfoRequest = (GetRoomInfoRequest)request;
                GameRoom room = roomManager.getRoom(getRoomInfoRequest.roomId);
                if (room == null) {
                    return new GetRoomInfoResponse(null);
                }
                return new GetRoomInfoResponse(room.getRoomInfo());
            }
            case GET_ROOMS: {
                GetRoomsListRequest getRoomsListRequest = (GetRoomsListRequest)request;
                return new GetRoomsListResponse(roomManager.getRooms(getRoomsListRequest.startPosition,
                        getRoomsListRequest.limit).stream().map(GameRoom::getRoomInfo).collect(Collectors.toList()),
                        roomManager.getRoomCount());
            }
            case JOIN_ROOM: {
                JoinRoomRequest joinRoomRequest = (JoinRoomRequest)request;
                long userId = playerManager.getUserIdByToken(joinRoomRequest.authToken);
                if (userId < 0) {
                    return new JoinRoomResponse(false);
                }
                return new JoinRoomResponse(roomManager.joinRoom(
                        playerManager.getPlayerById(userId),
                        joinRoomRequest.roomId));
            }
            case REGISTER_PLAYER: {
                RegisterPlayerRequest registerPlayerRequest = (RegisterPlayerRequest)request;
                long id = playerManager.getOrCreateUserId(registerPlayerRequest.name, registerPlayerRequest.password);
                long authToken = playerManager.getAuthToken(id, registerPlayerRequest.password);
                return new RegisterPlayerResponse(id, authToken);
            }
            case UPDATE_FIELD: {
                UpdateFieldRequest updateFieldRequest = (UpdateFieldRequest)request;
                GameRoom room = roomManager.getRoom(updateFieldRequest.roomId);
                if (room == null) {
                    return new UpdateFieldResponse(false);
                }
                return new UpdateFieldResponse(room.processDiff(
                        updateFieldRequest.diff, playerManager.getUserIdByToken(updateFieldRequest.authToken)));
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(4567);
        System.out.println("Server started");
        System.out.println(socket.getLocalSocketAddress());
        while (true) {
            Socket sock = socket.accept();
            INSTANCE.processConnection(sock);
        }
    }
}
