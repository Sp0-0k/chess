package ResponseException;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    private final int code;

    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        int status = (int) map.get("status");
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public int code() {
        return code;
    }
}