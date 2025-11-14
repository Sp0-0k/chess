package exception;

public class ResponseException extends Exception {
    private final int code;

    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int code() {
        return code;
    }
}