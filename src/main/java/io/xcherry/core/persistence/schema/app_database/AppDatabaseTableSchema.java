package io.xcherry.core.persistence.schema.app_database;

import io.xcherry.core.exception.persistence.AppDatabaseSchemaNotMatchException;
import io.xcherry.core.persistence.read_request.AppDatabaseTableReadRequest;
import io.xcherry.gen.models.DatabaseLockingType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseTableSchema {

    private final String tableName;
    private final DatabaseLockingType lockingType;
    private final AppDatabasePrimaryKeySchema primaryKeySchema;
    /**
     * column name: schema
     */
    private final Map<String, AppDatabaseColumnSchema> otherColumnSchemaMap;

    /**
     * Define an app database table schema.
     *
     * @param tableName             table name.
     * @param lockingType           locking type.
     * @param primaryKeySchema      primary key that contains column(s).
     * @param otherColumnSchemas    non-primary-key columns
     * @return  the created app database table schema.
     */
    public static AppDatabaseTableSchema define(
        final String tableName,
        final DatabaseLockingType lockingType,
        final AppDatabasePrimaryKeySchema primaryKeySchema,
        final AppDatabaseColumnSchema... otherColumnSchemas
    ) {
        final Map<String, AppDatabaseColumnSchema> otherColumnSchemaMap = new HashMap<>();

        for (final AppDatabaseColumnSchema columnSchema : otherColumnSchemas) {
            otherColumnSchemaMap.put(columnSchema.getColumnName(), columnSchema);
        }

        return new AppDatabaseTableSchema(tableName, lockingType, primaryKeySchema, otherColumnSchemaMap);
    }

    public String getTableName() {
        return tableName;
    }

    public Set<String> getPrimaryKeyColumns() {
        return primaryKeySchema.getColumnNames();
    }

    public Set<String> getOtherColumns() {
        return otherColumnSchemaMap.keySet();
    }

    public Class<?> getColumnValueType(final String columnName) {
        if (primaryKeySchema.contains(columnName)) {
            return primaryKeySchema.getColumnSchema(columnName).getValueType();
        }
        if (otherColumnSchemaMap.containsKey(columnName)) {
            return otherColumnSchemaMap.get(columnName).getValueType();
        }

        throw new AppDatabaseSchemaNotMatchException(
            String.format("Column %s is not defined in the table %s", columnName, tableName)
        );
    }

    public boolean isPrimaryKeyColumn(final String columnName) {
        return primaryKeySchema.contains(columnName);
    }

    public AppDatabaseTableReadRequest getReadRequest() {
        final Set<String> columnsToRead = new HashSet<>();

        // primary key columns will always be read.
        columnsToRead.addAll(primaryKeySchema.getColumnNames());

        otherColumnSchemaMap.forEach((columnName, columnSchema) -> {
            if (columnSchema.isLoadByDefault()) {
                columnsToRead.add(columnName);
            }
        });

        return AppDatabaseTableReadRequest.create(tableName, lockingType, columnsToRead);
    }
}
