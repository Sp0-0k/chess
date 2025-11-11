package ServerFacade;

import com.google.gson.Gson;
import com.google.gson.*;
import datamodel.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient reqClient = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData addUser(String username, String password, String email) throws Exception {
        var body = new UserData(username, password, email);
        var request = buildRequest("POST", "/user", body);
        var response = sendRequest(request);
        if (isSuccessful(response.statusCode())) {
            return new Gson().fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception("Failed From Server");
        }
    }

    public AuthData loginUser(String username, String password, String email) throws Exception {
        var body = new UserData(username, password, email);
        var request = buildRequest("POST", "/session", body);
        var response = sendRequest(request);
        if (isSuccessful(response.statusCode())) {
            return new Gson().fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception("Failed From Server");
        }
    }

    public void logoutUser() throws Exception {
        var request = buildRequest("DELETE", "/session", null);
        var response = sendRequest(request);
        if (!isSuccessful(response.statusCode())) {
            throw new Exception("Failed From Server");
        }
    }


    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return reqClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception("Failed");
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
