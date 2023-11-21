package io.xdb.core.exception;

public class RpcException extends RuntimeException {

    public RpcException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
