package io.xcherry.core.persistence.schema;

import io.xcherry.core.persistence.read_request.AppDatabaseReadRequest;
import io.xcherry.core.persistence.read_request.LocalAttributeReadRequest;
import io.xcherry.core.persistence.schema.app_database.AppDatabaseSchema;
import io.xcherry.core.persistence.schema.local_attribute.LocalAttributeSchema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceSchema {

    private final AppDatabaseSchema appDatabaseSchema;
    private final LocalAttributeSchema localAttributeSchema;

    /**
     * Create and return an empty persistence schema.
     *
     * @return  the created persistence schema.
     */
    public static PersistenceSchema EMPTY() {
        return PersistenceSchema.define(AppDatabaseSchema.EMPTY(), LocalAttributeSchema.EMPTY());
    }

    /**
     * Create and return a persistence schema.
     *
     * @param appDatabaseSchema     the app database schema.
     * @return  the created persistence schema.
     */
    public static PersistenceSchema define(final AppDatabaseSchema appDatabaseSchema) {
        return PersistenceSchema.define(appDatabaseSchema, LocalAttributeSchema.EMPTY());
    }

    /**
     * Create and return a persistence schema.
     *
     * @param localAttributeSchema  the local attribute schema.
     * @return  the created persistence schema.
     */
    public static PersistenceSchema define(final LocalAttributeSchema localAttributeSchema) {
        return PersistenceSchema.define(AppDatabaseSchema.EMPTY(), localAttributeSchema);
    }

    /**
     * Create and return a persistence schema.
     *
     * @param appDatabaseSchema     the app database schema.
     * @param localAttributeSchema  the local attribute schema.
     * @return  the created persistence schema.
     */
    public static PersistenceSchema define(
        final AppDatabaseSchema appDatabaseSchema,
        final LocalAttributeSchema localAttributeSchema
    ) {
        return new PersistenceSchema(appDatabaseSchema, localAttributeSchema);
    }

    public Class<?> getAppDatabaseColumnValueType(final String tableName, final String columnName) {
        return appDatabaseSchema.getColumnValueType(tableName, columnName);
    }

    public boolean isAppDatabasePrimaryKeyColumn(final String tableName, final String columnName) {
        return appDatabaseSchema.isPrimaryKeyColumn(tableName, columnName);
    }

    public AppDatabaseReadRequest getAppDatabaseReadRequest() {
        if (appDatabaseSchema == null) {
            return null;
        }

        return appDatabaseSchema.getReadRequest();
    }

    public Class<?> getLocalAttributeKeyValueType(final String key) {
        return localAttributeSchema.getKeyValueType(key);
    }

    public LocalAttributeReadRequest getLocalAttributeReadRequest() {
        if (localAttributeSchema == null) {
            return null;
        }

        return localAttributeSchema.getReadRequest();
    }
}
