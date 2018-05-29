package ru.spbau.mit.game.server;

import ru.spbau.mit.game.common.api.API;
import ru.spbau.mit.game.common.api.HttpRequest;
import ru.spbau.mit.game.common.api.HttpResponse;
import ru.spbau.mit.game.common.api.requests.Request;
import ru.spbau.mit.game.common.api.response.Response;
import ru.spbau.mit.game.server.exception.ServerException;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractConnectionPool {
    private ExecutorService executor = Executors.newCachedThreadPool();

    public void processConnection(Socket socket) {
        executor.execute(() -> {
            try {
                try {
                    HttpRequest request = HttpRequest.accept(socket);
                    Response response = processRequest(request.getRequest());
                    API.send(200, "OK", socket, response);
                } catch (ServerException e) {
                    API.send(e.code(), e.message(), socket, null);
                }
                socket.close();
            } catch (Exception e) {
                //TODO log
            }
        });
    }

    protected abstract Response processRequest(Request request);
}
