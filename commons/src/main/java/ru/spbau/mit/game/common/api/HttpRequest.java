package ru.spbau.mit.game.common.api;

import com.google.gson.Gson;
import ru.spbau.mit.game.common.api.requests.Request;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Gson gson = new Gson();

    private final Map<String, String> header;
    private final Request request;

    public HttpRequest(Map<String, String> header, Request request) {
        this.header = header;
        this.request = request;
    }

    public void send(Socket socket) throws IOException {
        final OutputStream outputStream = socket.getOutputStream();
        outputStream.write(buildHttp().getBytes("UTF-8"));
        outputStream.flush();
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Request getRequest() {
        return request;
    }

    public static HttpRequest accept(Socket socket) throws IOException {
        final Reader reader = new InputStreamReader(socket.getInputStream(), "UTF-8");
        final String[] titles = Utils.readLine(reader).split("\\s");
        final HttpRequestType httpType = HttpRequestType.valueOf(titles[0]);
        final API.Type type = API.findType(httpType, titles[1]);

        final HashMap<String, String> header = new HashMap<>();
        while (true) {
            final String line = Utils.readLine(reader).trim();
            if (line.isEmpty()) {
                break;
            }
            final int separatorIndex = line.indexOf(':');
            final String headerName = line.substring(0, separatorIndex);
            final String headerValue = line.substring(separatorIndex + 1).trim();
            header.put(headerName, headerValue);
        }

        final Request request = gson.fromJson(Utils.readLine(reader), type.requestClass);
        return new HttpRequest(header, request);
    }

    private String buildHttp() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.request.getDialogType().requestType)
                .append(" ")
                .append(this.request.getDialogType().uri)
                .append(" HTTP/1.1\n");
        header.forEach((key, value) -> builder.append(key).append(": ").append(value).append("\n"));
        builder.append("\n");
        builder.append(gson.toJson(request));
        builder.append("\n");
        return builder.toString();
    }
}
