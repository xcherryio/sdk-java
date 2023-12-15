package io.xcherry.core.exception.persistence;

public class AppDatabaseSchemaNotMatchException extends RuntimeException {

    public AppDatabaseSchemaNotMatchException(final String message) {
        super(message);
    }
}
