package uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network;

public class BookingResponse {

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    boolean error;
    String message;
    int code;

    public BookingResponse(boolean error, String message, int code) {
        this.error = error;
        this.message = message;
        this.code = code;
    }
}
