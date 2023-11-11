package io.xdb.core.persistence;

import com.google.common.collect.ImmutableMap;
import io.xdb.core.exception.GlobalAttributeNotFoundException;
import io.xdb.core.persistence.to_load.PersistenceTableSchemaToLoad;
import io.xdb.gen.models.TableReadLockingPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
     * @param primaryKeyColumn         the schema of a single column primary key.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema withSingleColumnPrimaryKey(
        final String tableName,
        final PersistenceTableColumnSchema primaryKeyColumn
    ) {
        return PersistenceTableSchema.withSingleColumnPrimaryKey(
            tableName,
            primaryKeyColumn,
            TableReadLockingPolicy.NO_LOCKING
        );
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema}.
     *
     * @param tableName                 table name.
     * @param primaryKeyColumn          the schema of a single column primary key.
     * @param tableReadLockingPolicy    locking policy when reading the table.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema withSingleColumnPrimaryKey(
        final String tableName,
        final PersistenceTableColumnSchema primaryKeyColumn,
        final TableReadLockingPolicy tableReadLockingPolicy
    ) {
        return new PersistenceTableSchema(
            tableName,
            ImmutableMap.of(primaryKeyColumn.getColumnName(), primaryKeyColumn),
            new HashMap<>(),
            tableReadLockingPolicy
        );
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema} with the default NO_LOCKING reading policy.
     *
     * @param tableName                 table name.
     * @param primaryKeyColumns         the schema of a multiple columns primary key.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema withMultipleColumnsPrimaryKey(
        final String tableName,
        final List<PersistenceTableColumnSchema> primaryKeyColumns
    ) {
        return PersistenceTableSchema.withMultipleColumnsPrimaryKey(
            tableName,
            primaryKeyColumns,
            TableReadLockingPolicy.NO_LOCKING
        );
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema}.
     *
     * @param tableName                 table name.
     * @param primaryKeyColumns         the schema of a multiple columns primary key.
     * @param tableReadLockingPolicy    locking policy when reading the table.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema withMultipleColumnsPrimaryKey(
        final String tableName,
        final List<PersistenceTableColumnSchema> primaryKeyColumns,
        final TableReadLockingPolicy tableReadLockingPolicy
    ) {
        return new PersistenceTableSchema(
            tableName,
            primaryKeyColumns
                .stream()
                .collect(Collectors.toMap(PersistenceTableColumnSchema::getColumnName, Function.identity())),
            new HashMap<>(),
            tableReadLockingPolicy
        );
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema} without the primary key, and with the default NO_LOCKING reading policy.
     *
     * @param tableName                 table name.
     * @param columns                   the schema of non-primary-key columns.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema noPrimaryKey(
        final String tableName,
        final List<PersistenceTableColumnSchema> columns
    ) {
        return PersistenceTableSchema.noPrimaryKey(tableName, columns, TableReadLockingPolicy.NO_LOCKING);
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema} without the primary key.
     *
     * @param tableName                 table name.
     * @param columns                   the schema of non-primary-key columns.
     * @param tableReadLockingPolicy    locking policy when reading the table.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema noPrimaryKey(
        final String tableName,
        final List<PersistenceTableColumnSchema> columns,
        final TableReadLockingPolicy tableReadLockingPolicy
    ) {
        return new PersistenceTableSchema(
            tableName,
            new HashMap<>(),
            columns
                .stream()
                .collect(Collectors.toMap(PersistenceTableColumnSchema::getColumnName, Function.identity())),
            tableReadLockingPolicy
        );
    }

    /**
     * Add a non-primary-key column schema into the table schema.
     *
     * @param column    the schema of a non-primary-key column.
     * @return  the updated table schema.
     */
    public PersistenceTableSchema addColumn(final PersistenceTableColumnSchema column) {
        this.otherColumns.put(column.getColumnName(), column);
        return this;
    }

    public Class<?> getColumnValueType(final String columnName) {
        if (primaryKeyColumns.containsKey(columnName)) {
            return primaryKeyColumns.get(columnName).getValueType();
        }
        if (otherColumns.containsKey(columnName)) {
            return otherColumns.get(columnName).getValueType();
        }

        throw new GlobalAttributeNotFoundException(
            String.format(
                "Column %s does not exist in the table %s within the global attributes",
                columnName,
                tableName
            )
        );
    }

    public PersistenceTableSchemaToLoad getPersistenceTableSchemaToLoad() {
        final List<String> columnsToLoad = new ArrayList<>();

        primaryKeyColumns.forEach((columnName, columnSchema) -> {
            if (columnSchema.isLoadByDefault()) {
                columnsToLoad.add(columnName);
            }
        });

        otherColumns.forEach((columnName, columnSchema) -> {
            if (columnSchema.isLoadByDefault()) {
                columnsToLoad.add(columnName);
            }
        });

        return PersistenceTableSchemaToLoad.create(tableName, columnsToLoad, tableReadLockingPolicy);
    }
}
