package io.xcherry.core.persistence.schema_to_load;

import io.xcherry.gen.models.DatabaseLockingType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceTableSchemaToLoadData {

    private final String tableName;
    private final List<String> columnNames;
    private final DatabaseLockingType databaseLockingType;

    /**
     * Create a table schema to load with the default NO_LOCKING reading policy.
     *
     * @param tableName     table name.
     * @param columnNames   the columns to load.
     * @return  the created table schema to load.
     */
    public static PersistenceTableSchemaToLoadData create(final String tableName, final String... columnNames) {
        return PersistenceTableSchemaToLoadData.create(
            tableName,
            Arrays.stream(columnNames).collect(Collectors.toList())
        );
    }

    /**
     * Create a table schema to load with the default NO_LOCKING reading policy.
     *
     * @param tableName     table name.
     * @param columnNames   the columns to load.
     * @return  the created table schema to load.
     */
    public static PersistenceTableSchemaToLoadData create(final String tableName, final List<String> columnNames) {
        return PersistenceTableSchemaToLoadData.create(tableName, columnNames, DatabaseLockingType.NO_LOCKING);
    }

    /**
     * Create a table schema to load.
     *
     * @param tableName                 table name.
     * @param columnNames               the columns to load.
     * @param databaseLockingType       locking policy when reading the database.
     * @return  the created table schema to load.
     */
    public static PersistenceTableSchemaToLoadData create(
        final String tableName,
        final List<String> columnNames,
        final DatabaseLockingType databaseLockingType
    ) {
        return new PersistenceTableSchemaToLoadData(tableName, columnNames, databaseLockingType);
    }
}
