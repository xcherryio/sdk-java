package io.xcherry.core.persistence.schema.app_database;

import io.xcherry.core.exception.persistence.AppDatabaseSchemaNotMatchException;
import io.xcherry.core.persistence.read_request.AppDatabaseReadRequest;
import io.xcherry.core.persistence.read_request.AppDatabaseTableReadRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseSchema {

    /**
     * table name: schema
     */
    private final Map<String, AppDatabaseTableSchema> tableSchemaMap;

    /**
     * Define an empty app database schema.
     *
     * @return  the created app database schema.
     */
    public static AppDatabaseSchema EMPTY() {
        return AppDatabaseSchema.define();
    }

    /**
     * Define an app database schema.
     *
     * @param tableSchemas  a list of {@link AppDatabaseTableSchema}.
     * @return  the created app database schema.
     */
    public static AppDatabaseSchema define(final AppDatabaseTableSchema... tableSchemas) {
        final Map<String, AppDatabaseTableSchema> tableSchemaMap = new HashMap<>();

        for (final AppDatabaseTableSchema tableSchema : tableSchemas) {
            tableSchemaMap.put(tableSchema.getTableName(), tableSchema);
        }

        return new AppDatabaseSchema(tableSchemaMap);
    }

    public Map<String, AppDatabaseTableSchema> getTableSchemaMap() {
        return tableSchemaMap;
    }

    public Class<?> getColumnValueType(final String tableName, final String columnName) {
        if (!tableSchemaMap.containsKey(tableName)) {
            throw new AppDatabaseSchemaNotMatchException(
                String.format("Table %s is not defined in the persistence schema", tableName)
            );
        }

        return tableSchemaMap.get(tableName).getColumnValueType(columnName);
    }

    public boolean isPrimaryKeyColumn(final String tableName, final String columnName) {
        if (!tableSchemaMap.containsKey(tableName)) {
            throw new AppDatabaseSchemaNotMatchException(
                String.format("Table %s is not defined in the persistence schema", tableName)
            );
        }

        return tableSchemaMap.get(tableName).isPrimaryKeyColumn(columnName);
    }

    public AppDatabaseReadRequest getReadRequest() {
        if (tableSchemaMap.isEmpty()) {
            return null;
        }

        final List<AppDatabaseTableReadRequest> tableReadRequests = new ArrayList<>();

        tableSchemaMap.forEach((tableName, tableSchema) -> {
            tableReadRequests.add(tableSchema.getReadRequest());
        });

        return AppDatabaseReadRequest.create(tableReadRequests);
    }
}
