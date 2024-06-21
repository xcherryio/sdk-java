package io.xcherry.core.exception;

public class WaitTimeoutException extends RuntimeException {

    public WaitTimeoutException(final String message) {
        super(message);
    }
}
