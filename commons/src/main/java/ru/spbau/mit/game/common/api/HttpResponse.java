package ru.spbau.mit.game.common.api;

import com.google.gson.Gson;
import ru.spbau.mit.game.common.api.response.Response;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Gson gson = new Gson();

    private final int resultCode;
    private final String resultDescription;
    private final Map<String, String> header;
    private final Response response;

    public HttpResponse(int resultCode, String resultDescription, Map<String, String> header, Response response) {
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
        this.header = header;
        this.response = response;
    }

    public void send(Socket socket) throws IOException {
        final OutputStream outputStream = socket.getOutputStream();
        outputStream.write(buildHttp().getBytes("UTF-8"));
        outputStream.flush();
    }

    public Response getResponse() {
        return response;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public static HttpResponse accept(Socket socket, API.Type type) throws IOException {
        final Reader reader = new InputStreamReader(socket.getInputStream(), "UTF-8");
        final String[] titles = Utils.readLine(reader).split("\\s");
        final int resultCode = Integer.parseInt(titles[1]);
        final String resultDescription = titles.length == 2 ? "" : titles[2];

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
        final Response response = gson.fromJson(Utils.readLine(reader), type.responseClass);
        return new HttpResponse(resultCode, resultDescription, header, response);
    }

    private String buildHttp() {
        final StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1")
                .append(" ")
                .append(resultCode)
                .append(" ")
                .append(resultDescription)
                .append("\n");
        header.forEach((key, value) -> builder.append(key).append(": ").append(value).append("\n"));
        builder.append("\n");
        builder.append(gson.toJson(response));
        builder.append("\n");
        return builder.toString();
    }
}
