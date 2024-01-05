package io.xcherry.core.persistence.schema;

import io.xcherry.core.persistence.readrequest.AppDatabaseReadRequest;
import io.xcherry.core.persistence.readrequest.LocalAttributeReadRequest;
import io.xcherry.core.persistence.schema.appdatabase.AppDatabaseSchema;
import io.xcherry.core.persistence.schema.localattribute.LocalAttributeSchema;
import io.xcherry.core.rpc.RpcPersistenceReadRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceSchema {

    private final AppDatabaseSchema appDatabaseSchema;
    private final LocalAttributeSchema localAttributeSchema;

    /**
     * name: rpcPersistenceReadRequest
     */
    private final Map<String, RpcPersistenceReadRequest> rpcPersistenceReadRequestMap;

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
     * @param appDatabaseSchema             the app database schema.
     * @param localAttributeSchema          the local attribute schema.
     * @param rpcPersistenceReadRequests    a list of {@link RpcPersistenceReadRequest} to be used in @{@link io.xcherry.core.rpc.RPC} methods.
     * @return  the created persistence schema.
     */
    public static PersistenceSchema define(
        final AppDatabaseSchema appDatabaseSchema,
        final LocalAttributeSchema localAttributeSchema,
        final RpcPersistenceReadRequest... rpcPersistenceReadRequests
    ) {
        final Map<String, RpcPersistenceReadRequest> rpcPersistenceReadRequestMap = new HashMap<>();

        for (final RpcPersistenceReadRequest rpcPersistenceReadRequest : rpcPersistenceReadRequests) {
            rpcPersistenceReadRequestMap.put(rpcPersistenceReadRequest.getName(), rpcPersistenceReadRequest);
        }

        return new PersistenceSchema(appDatabaseSchema, localAttributeSchema, rpcPersistenceReadRequestMap);
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
