package ru.spbau.mit.game.common.api;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import ru.spbau.mit.game.common.api.requests.*;
import ru.spbau.mit.game.common.api.response.*;
import ru.spbau.mit.game.common.api.units.Room;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;

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

    public static Response request(Request request, String host, int port) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            final HttpRequest httpRequest = new HttpRequest(singletonMap("Host", host), request);
            httpRequest.send(socket);
            final HttpResponse response = HttpResponse.accept(socket, Type.GET_ROOMS);
            return response.getResponse();
        }
    }

    public static void send(Response response, int code, String description, Socket socket) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(code, description,
                singletonMap("Host", socket.getInetAddress().getHostName()), response);
        httpResponse.send(socket);
    }

    public enum Type {
        REGISTER_PLAYER(HttpRequestType.POST, "players/list", RegisterPlayerRequest.class, RegisterPlayerResponse.class),
        GET_ROOMS(HttpRequestType.GET, "rooms/list", GetRoomsListRequest.class, GetRoomsListResponse.class),
        GET_ROOM_INFO(HttpRequestType.GET, "rooms/statuses", GetRoomInfoRequest.class, GetRoomsListResponse.class),
        JOIN_ROOM(HttpRequestType.POST, "rooms/statuses", JoinRoomRequest.class, JoinRoomResponse.class),
        CREATE_ROOM(HttpRequestType.POST, "rooms/list", CreateRoomRequest.class, CreateRoomResponse.class),
        DELETE_ROOM(HttpRequestType.DELETE, "rooms/list", DeleteRoomRequest.class, DeleteRoomResponse.class),
        GET_FIELD(HttpRequestType.GET, "rooms/fields", GetFieldRequest.class, GetFieldResponse.class),
        UPDATE_FIELD(HttpRequestType.PATCH, "rooms/fields", UpdateFieldRequest.class, UpdateFieldResponse.class);

        public final HttpRequestType requestType;
        public final String uri;
        public final Class<? extends Request> requestClass;
        public final Class<? extends Response> responseClass;

        Type(HttpRequestType requestType, String uri, Class<? extends Request> requestClass, Class<? extends Response> responseClass) {
            this.requestType = requestType;
            this.uri = uri;
            this.requestClass = requestClass;
            this.responseClass = responseClass;
        }
    }
}
