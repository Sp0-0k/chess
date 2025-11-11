package ServerFacade;

import ResponseException.ResponseException;
import com.google.gson.Gson;
import com.google.gson.*;
import datamodel.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private final HttpClient reqClient = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData addUser(String username, String password, String email) throws ResponseException {
        var body = new UserData(username, password, email);
        var request = buildRequest("POST", "/user", body);
        var response = sendRequest(request);
        if (isSuccessful(response.statusCode())) {
            return new Gson().fromJson(response.body(), AuthData.class);
        } else {
            throw new ResponseException(response.statusCode(), "Failed from server:" + response.body() + response.statusCode());
        }
    }

    public AuthData loginUser(String username, String password) throws ResponseException {
        var body = Map.of("username", username, "password", password);
        var request = buildRequest("POST", "/session", body);
        var response = sendRequest(request);
        if (isSuccessful(response.statusCode())) {
            return new Gson().fromJson(response.body(), AuthData.class);
        } else {
            throw new ResponseException(response.statusCode(), "Filed from server:" + response.body() + response.statusCode());
        }
    }

    public void logoutUser() throws ResponseException {
        var request = buildRequest("DELETE", "/session", null);
        var response = sendRequest(request);
        if (!isSuccessful(response.statusCode())) {
            throw new ResponseException(response.statusCode(), "Failed from server:" + response.body() + response.statusCode());
        }
    }


    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return reqClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(400, "Failed to send request");
        }
    }


    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(body));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
