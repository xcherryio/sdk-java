package io.xcherry.core.exception.persistence;

public class AppDatabaseNotFoundException extends RuntimeException {

    public AppDatabaseNotFoundException(final String message) {
        super(message);
    }
}
