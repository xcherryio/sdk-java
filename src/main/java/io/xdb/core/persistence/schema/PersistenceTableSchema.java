package io.xdb.core.persistence.schema;

import io.xdb.core.exception.persistence.GlobalAttributeNotFoundException;
import io.xdb.core.persistence.schema_to_load.PersistenceTableSchemaToLoadData;
import io.xdb.gen.models.TableReadLockingPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceTableSchema {

    private final String tableName;
    /**
     * column name : column schema
     */
    private final Map<String, PersistenceTableColumnSchema> primaryKeyColumns;
    /**
     * column name : column schema
     */
    private final Map<String, PersistenceTableColumnSchema> otherColumns;
    private final TableReadLockingPolicy tableReadLockingPolicy;

    /**
     * Create a table schema to be used in {@link PersistenceSchema} with the default NO_LOCKING reading policy.
     *
     * @param tableName                table name.
     * @param tableColumnSchemas       the schema of all the table columns.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema create(
        final String tableName,
        final PersistenceTableColumnSchema... tableColumnSchemas
    ) {
        return PersistenceTableSchema.create(tableName, TableReadLockingPolicy.NO_LOCKING, tableColumnSchemas);
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema}.
     *
     * @param tableName                 table name.
     * @param tableReadLockingPolicy    locking policy when reading the table.
     * @param tableColumnSchemas       the schema of all the table columns.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema create(
        final String tableName,
        final TableReadLockingPolicy tableReadLockingPolicy,
        final PersistenceTableColumnSchema... tableColumnSchemas
    ) {
        final Map<String, PersistenceTableColumnSchema> primaryKeyColumns = new HashMap<>();
        final Map<String, PersistenceTableColumnSchema> otherColumns = new HashMap<>();

        for (final PersistenceTableColumnSchema tableColumnSchema : tableColumnSchemas) {
            if (tableColumnSchema.isPrimaryKey()) {
                primaryKeyColumns.put(tableColumnSchema.getColumnName(), tableColumnSchema);
            } else {
                otherColumns.put(tableColumnSchema.getColumnName(), tableColumnSchema);
            }
        }

        return new PersistenceTableSchema(tableName, primaryKeyColumns, otherColumns, tableReadLockingPolicy);
    }

    public Class<?> getColumnValueType(final String columnName) {
        if (primaryKeyColumns.containsKey(columnName)) {
            return primaryKeyColumns.get(columnName).getValueType();
        }
        if (otherColumns.containsKey(columnName)) {
            return otherColumns.get(columnName).getValueType();
        }

        throw new GlobalAttributeNotFoundException(
            String.format("Column %s does not exist in the table %s", columnName, tableName)
        );
    }

    public PersistenceTableSchemaToLoadData getPersistenceTableSchemaToLoadData() {
        final List<String> columnsToLoadData = new ArrayList<>();

        primaryKeyColumns.forEach((columnName, columnSchema) -> {
            if (columnSchema.isLoadByDefault()) {
                columnsToLoadData.add(columnName);
            }
        });

        otherColumns.forEach((columnName, columnSchema) -> {
            if (columnSchema.isLoadByDefault()) {
                columnsToLoadData.add(columnName);
            }
        });

        return PersistenceTableSchemaToLoadData.create(tableName, columnsToLoadData, tableReadLockingPolicy);
    }
}
