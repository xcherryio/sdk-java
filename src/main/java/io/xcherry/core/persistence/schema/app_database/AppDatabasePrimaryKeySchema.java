package io.xcherry.core.persistence.schema.app_database;

import io.xcherry.core.exception.persistence.AppDatabaseSchemaNotMatchException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabasePrimaryKeySchema {

    /**
     * column name: schema
     */
    private final Map<String, AppDatabaseColumnSchema> columnSchemaMap;

    /**
     * Define an app database table primary key schema.
     * The columns in primary key will always be loaded.
     *
     * @param columnSchemas   a list of {@link AppDatabaseColumnSchema} that consist of the primary key.
     * @return the defined app database table primary key schema.
     */
    public static AppDatabasePrimaryKeySchema define(final AppDatabaseColumnSchema... columnSchemas) {
        final Map<String, AppDatabaseColumnSchema> columnSchemaMap = new HashMap<>();

        for (final AppDatabaseColumnSchema columnSchema : columnSchemas) {
            columnSchemaMap.put(columnSchema.getColumnName(), columnSchema);
        }

        return new AppDatabasePrimaryKeySchema(columnSchemaMap);
    }

    public Set<String> getColumnNames() {
        return columnSchemaMap.keySet();
    }

    public boolean contains(final String columnName) {
        return columnSchemaMap.containsKey(columnName);
    }

    public AppDatabaseColumnSchema getColumnSchema(final String columnName) {
        if (!contains(columnName)) {
            throw new AppDatabaseSchemaNotMatchException(
                String.format("Column %s does not exist in the primary key.", columnName)
            );
        }

        return columnSchemaMap.get(columnName);
    }
}
