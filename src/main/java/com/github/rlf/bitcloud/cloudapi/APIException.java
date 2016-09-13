package com.github.rlf.bitcloud.cloudapi;

/**
 * An exception when accessing the CloudAPI
 */
public class APIException extends RuntimeException {
    enum Reason {
        CONNECTION_ERROR,
        INVALID_CONTENT,
        GENERAL_ERROR
    }

    private final Reason reason;

    public APIException(Throwable cause) {
        this(cause, Reason.GENERAL_ERROR);
    }

    public APIException(Throwable cause, Reason reason) {
        super(cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "APIException{" +
                "reason=" + reason +
                ", cause=" + getCause() +
                '}';
    }
}
