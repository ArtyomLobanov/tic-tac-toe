package ru.spbau.mit.game.common.api;

import ru.spbau.mit.game.common.api.requests.*;
import ru.spbau.mit.game.common.api.response.*;

import java.io.IOException;
import java.net.Socket;

import static java.util.Collections.singletonMap;

public class API {

    static Type findType(HttpRequestType httpType, String uri) {
        for (Type type : Type.values()) {
            if (type.requestType.equals(httpType) && type.uri.equals(uri)) {
                return type;
            }
        }
        throw new RuntimeException("Unknown api call!");
    }

    public static Response request(ServerAddress address, Request request) throws IOException, RequestException {
        try (Socket socket = new Socket(address.host, address.port)) {
            final HttpRequest httpRequest = new HttpRequest(singletonMap("Host", address.host), request);
            httpRequest.send(socket);
            final HttpResponse response = HttpResponse.accept(socket, request.getDialogType());
            if (response.getResultCode() >= 300) {
                throw new RequestException(response.getResultDescription(), response.getResultCode());
            }
            return response.getResponse();
        }
    }

    public static void send(int code, String description, Socket socket, Response response) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(code, description,
                singletonMap("Host", socket.getInetAddress().getHostName()), response);
        httpResponse.send(socket);
    }

    public enum Type {
        REGISTER_PLAYER(HttpRequestType.POST, "players/list", RegisterPlayerRequest.class,
                RegisterPlayerResponse.class),
        GET_ROOMS(HttpRequestType.GET, "rooms/list", GetRoomsListRequest.class, GetRoomsListResponse.class),
        GET_ROOM_INFO(HttpRequestType.GET, "rooms/statuses", GetRoomInfoRequest.class, GetRoomInfoResponse.class),
        JOIN_ROOM(HttpRequestType.POST, "rooms/statuses", JoinRoomRequest.class, JoinRoomResponse.class),
        CREATE_ROOM(HttpRequestType.POST, "rooms/list", CreateRoomRequest.class, CreateRoomResponse.class),
        DELETE_ROOM(HttpRequestType.DELETE, "rooms/list", DeleteRoomRequest.class, DeleteRoomResponse.class),
        GET_FIELD(HttpRequestType.GET, "rooms/fields", GetFieldRequest.class, GetFieldResponse.class),
        GET_FIELD_PATCH(HttpRequestType.GET, "rooms/fields/patches", GetFieldPatchRequest.class,
                GetFieldPatchResponse.class),
        UPDATE_FIELD(HttpRequestType.PATCH, "rooms/fields", UpdateFieldRequest.class, UpdateFieldResponse.class);

        public final HttpRequestType requestType;
        public final String uri;
        public final Class<? extends Request> requestClass;
        public final Class<? extends Response> responseClass;

        Type(HttpRequestType requestType, String uri, Class<? extends Request> requestClass,
             Class<? extends Response> responseClass) {
            this.requestType = requestType;
            this.uri = uri;
            this.requestClass = requestClass;
            this.responseClass = responseClass;
        }
    }

    public static class RequestException extends Exception {
        private final int code;

        public RequestException(String message, int code) {
            super(message);
            this.code = code;
        }
    }
}
