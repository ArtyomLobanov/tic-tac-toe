package ru.spbau.mit.game.client;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.HttpRequest;
import ru.spbau.mit.game.common.api.requests.GetFieldPatchRequest;
import ru.spbau.mit.game.common.api.requests.RegisterPlayerRequest;
import ru.spbau.mit.game.common.api.requests.UpdateFieldRequest;
import ru.spbau.mit.game.common.api.response.*;
import ru.spbau.mit.game.common.api.units.Field;
import ru.spbau.mit.game.common.api.units.Player;
import ru.spbau.mit.game.common.api.units.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(4567);
        Field.Cell[][] cells = new Field.Cell[15][15];
        Player first = new Player("p1", 1);
        Player second = new Player("p2", 2);
        final List<Field.Diff> diffs = new ArrayList<>();
        int cnt = 0;
        int odd = 0;
        int userId = 0;
        while (true) {
            Socket input = socket.accept();
            final HttpRequest request = HttpRequest.accept(input);
            if (request.getRequest().getDialogType() == API.Type.GET_FIELD) {
                API.send(200, "OK", input, new GetFieldResponse(new Field(cells, first, second, odd)));
            } else if (request.getRequest().getDialogType() == API.Type.GET_ROOM_INFO) {
                API.send(200, "OK", input,
                        new GetRoomInfoResponse(new Room("room1", 12, new Player("p1", 1),
                                new Player("p1", 2), 15, Room.Status.IN_PROGRESS, true))
                        );
            } else if (request.getRequest().getDialogType() == API.Type.UPDATE_FIELD) {
                odd++;
                System.out.println("parsed!");
                UpdateFieldRequest update = (UpdateFieldRequest) request.getRequest();
                diffs.add(update.diff);
                cells[update.diff.row][update.diff.column] = update.diff.cell;
                API.send(200, "OK", input, new UpdateFieldResponse(true));
            } else if (request.getRequest().getDialogType() == API.Type.GET_FIELD_PATCH) {
                GetFieldPatchRequest patchRequest = (GetFieldPatchRequest) request.getRequest();
                API.send(200, "OK", input,
                        new GetFieldPatchResponse(new Field.Patch(patchRequest.startVersion, odd,
                                diffs.subList(patchRequest.startVersion, odd))));
            } else if (request.getRequest().getDialogType() == API.Type.REGISTER_PLAYER) {
                RegisterPlayerRequest registerRequest = (RegisterPlayerRequest) request.getRequest();
                API.send(200, "OK", input,
                        new RegisterPlayerResponse(userId++));
            } else if (request.getRequest().getDialogType() == API.Type.GET_ROOMS) {
                API.send(200, "OK", input,
                        new GetRoomsListResponse(new Room[]{
                                new Room("name", 23, new Player("pla1", 12),
                                        null, 3, Room.Status.WAIT_GUEST, false),
                                new Room("best room ever", 235, new Player("pla1", 12),
                                        null, 15, Room.Status.WAIT_GUEST, false)}));
            }
            input.close();
            System.out.println("send");
        }
    }
}
