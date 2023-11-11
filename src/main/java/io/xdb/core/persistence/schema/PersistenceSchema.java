package io.xdb.core.persistence.schema;

import io.xdb.core.exception.GlobalAttributeNotFoundException;
import io.xdb.core.persistence.schema_to_load.PersistenceSchemaToLoad;
import io.xdb.core.persistence.schema_to_load.PersistenceTableSchemaToLoad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceSchema {

    /**
     * table name: table schema
     */
    private final Map<String, PersistenceTableSchema> globalAttributes = new HashMap<>();

    /**
     * Create and return an empty persistence schema.
     *
     * @return  the created persistence schema.
     */
    public static PersistenceSchema EMPTY() {
        return new PersistenceSchema();
    }

    /**
     * Create and return a persistence schema with global attributes.
     *
     * @param persistenceTableSchemas    the table schemas of global attributes.
     * @return  the created persistence schema.
     */
    public static PersistenceSchema withGlobalAttributes(final PersistenceTableSchema... persistenceTableSchemas) {
        return PersistenceSchema.EMPTY().addGlobalAttributes(persistenceTableSchemas);
    }

    /**
     * Update the persistence schema with global attributes and return the new persistence schema.
     *
     * @param persistenceTableSchemas    the table schemas of global attributes.
     * @return the updated persistence schema.
     */
    public PersistenceSchema addGlobalAttributes(final PersistenceTableSchema... persistenceTableSchemas) {
        for (final PersistenceTableSchema persistenceTableSchema : persistenceTableSchemas) {
            globalAttributes.put(persistenceTableSchema.getTableName(), persistenceTableSchema);
        }
        return this;
    }

    public Class<?> getGlobalAttributeColumnValueType(final String tableName, final String columnName) {
        if (!globalAttributes.containsKey(tableName)) {
            throw new GlobalAttributeNotFoundException(
                String.format("Table %s does not exist within the global attributes", tableName)
            );
        }

        return globalAttributes.get(tableName).getColumnValueType(columnName);
    }

    public PersistenceSchemaToLoad getPersistenceSchemaToLoad() {
        final List<PersistenceTableSchemaToLoad> tableSchemasToLoad = new ArrayList<>();

        globalAttributes.forEach((table, tableSchema) -> {
            tableSchemasToLoad.add(tableSchema.getPersistenceTableSchemaToLoad());
        });

        return PersistenceSchemaToLoad.withGlobalAttributes(tableSchemasToLoad);
    }
}
