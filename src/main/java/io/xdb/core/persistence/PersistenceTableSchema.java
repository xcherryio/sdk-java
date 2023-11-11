package io.xdb.core.persistence;

import io.xdb.gen.models.TableReadLockingPolicy;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceTableSchema {

    private final String tableName;
    private final String primaryKeyColumnName;
    private final Set<String> otherColumnNames;
    private final TableReadLockingPolicy tableReadLockingPolicy;

    /**
     * Create a table schema to be used in {@link PersistenceSchema} with the default NO_LOCKING reading policy.
     *
     * @param tableName                 table name.
     * @param primaryKeyColumnName      the column name of the primary key.
     * @param otherColumnNames          other non-primary-key column names.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema withPrimaryKey(
        final String tableName,
        final String primaryKeyColumnName,
        final Set<String> otherColumnNames
    ) {
        return PersistenceTableSchema.withPrimaryKey(
            tableName,
            primaryKeyColumnName,
            otherColumnNames,
            TableReadLockingPolicy.NO_LOCKING
        );
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema}.
     *
     * @param tableName                 table name.
     * @param primaryKeyColumnName      the column name of the primary key.
     * @param otherColumnNames          other non-primary-key column names.
     * @param tableReadLockingPolicy    locking policy when reading the table.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema withPrimaryKey(
        final String tableName,
        final String primaryKeyColumnName,
        final Set<String> otherColumnNames,
        final TableReadLockingPolicy tableReadLockingPolicy
    ) {
        return new PersistenceTableSchema(tableName, primaryKeyColumnName, otherColumnNames, tableReadLockingPolicy);
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema} without the primary key, and with the default NO_LOCKING reading policy.
     *
     * @param tableName                 table name.
     * @param otherColumnNames          other non-primary-key column names.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema noPrimaryKey(final String tableName, final Set<String> otherColumnNames) {
        return PersistenceTableSchema.noPrimaryKey(tableName, otherColumnNames, TableReadLockingPolicy.NO_LOCKING);
    }

    /**
     * Create a table schema to be used in {@link PersistenceSchema} without the primary key.
     *
     * @param tableName                 table name.
     * @param otherColumnNames          other non-primary-key column names.
     * @param tableReadLockingPolicy    locking policy when reading the table.
     * @return  the created table schema.
     */
    public static PersistenceTableSchema noPrimaryKey(
        final String tableName,
        final Set<String> otherColumnNames,
        final TableReadLockingPolicy tableReadLockingPolicy
    ) {
        return new PersistenceTableSchema(tableName, null, otherColumnNames, tableReadLockingPolicy);
    }

    /**
     * Add a non-primary-key column name in the table schema.
     *
     * @param columnName    the name of the column.
     * @return  the updated table schema.
     */
    public PersistenceTableSchema addColumnName(final String columnName) {
        this.otherColumnNames.add(columnName);
        return this;
    }
}
