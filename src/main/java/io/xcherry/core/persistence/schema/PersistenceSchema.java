package io.xcherry.core.persistence.schema;

import io.xcherry.core.persistence.read_request.AppDatabaseReadRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceSchema {

    private final AppDatabaseSchema appDatabaseSchema;

    /**
     * Create and return an empty persistence schema.
     *
     * @return  the created persistence schema.
     */
    public static PersistenceSchema EMPTY() {
        return PersistenceSchema.define(AppDatabaseSchema.EMPTY());
    }

    /**
     * Create and return a persistence schema.
     *
     * @param appDatabaseSchema   the app database.
     * @return  the created persistence schema.
     */
    public static PersistenceSchema define(final AppDatabaseSchema appDatabaseSchema) {
        return new PersistenceSchema(appDatabaseSchema);
    }

    public AppDatabaseSchema getAppDatabaseSchema() {
        return appDatabaseSchema;
    }

    public Class<?> getAppDatabaseColumnValueType(final String tableName, final String columnName) {
        return appDatabaseSchema.getColumnValueType(tableName, columnName);
    }

    public boolean isAppDatabasePrimaryKeyColumn(final String tableName, final String columnName) {
        return appDatabaseSchema.isPrimaryKeyColumn(tableName, columnName);
    }

    public AppDatabaseReadRequest getAppDatabaseReadRequest() {
        return appDatabaseSchema.getReadRequest();
    }
}
