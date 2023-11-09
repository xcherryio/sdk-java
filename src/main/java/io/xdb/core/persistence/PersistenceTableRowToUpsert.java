package io.xdb.core.persistence;

import io.xdb.gen.models.AttributeWriteConflictMode;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class PersistenceTableRowToUpsert {

    private final String tableName;
    String primaryKeyColumnName = null;
    String primaryKeyColumnValue = null;
    /**
     * key: column name
     * value: column value
     */
    private final Map<String, String> otherColumns = new HashMap<>();
    private final AttributeWriteConflictMode writeConflictMode;

    private PersistenceTableRowToUpsert(final String tableName, final AttributeWriteConflictMode writeConflictMode) {
        this.tableName = tableName;
        this.writeConflictMode = writeConflictMode;
    }

    /**
     * Create a table row to upsert in persistence.
     *
     * @param tableName             the name of the table.
     * @param writeConflictMode     the mode to choose when there is a conflict in upsert.
     * @return  the table row to upsert.
     */
    public static PersistenceTableRowToUpsert create(
        final String tableName,
        final AttributeWriteConflictMode writeConflictMode
    ) {
        return new PersistenceTableRowToUpsert(tableName, writeConflictMode);
    }

    /**
     * Create a table row to upsert in persistence with the primary key.
     *
     * @param tableName             the name of the table.
     * @param columnName            the column name of the primary key.
     * @param columnValue           the column value of the primary key.
     * @param writeConflictMode     the mode to choose when there is a conflict in upsert.
     *
     * @return  the table row to upsert.
     */
    public static PersistenceTableRowToUpsert createWithPrimaryKeyColumn(
        final String tableName,
        final String columnName,
        final String columnValue,
        final AttributeWriteConflictMode writeConflictMode
    ) {
        return new PersistenceTableRowToUpsert(tableName, writeConflictMode)
            .setPrimaryKeyColumn(columnName, columnValue);
    }

    /**
     * Set the primary key of the table row to upsert.
     *
     * @param columnName    the name of the primary key column.
     * @param value         the value of the primary key column.
     * @return  the updated table row.
     */
    public PersistenceTableRowToUpsert setPrimaryKeyColumn(final String columnName, final String value) {
        primaryKeyColumnName = columnName;
        primaryKeyColumnValue = value;
        return this;
    }

    /**
     * Add a non-primary-key column of the table row to upsert.
     *
     * @param columnName    the name of the column.
     * @param value         the value of the column.
     * @return  the updated table row.
     */
    public PersistenceTableRowToUpsert addNonPrimaryKeyColumn(final String columnName, final String value) {
        otherColumns.put(columnName, value);
        return this;
    }
}
