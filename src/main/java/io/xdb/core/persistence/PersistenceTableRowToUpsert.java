package io.xdb.core.persistence;

import io.xdb.gen.models.AttributeWriteConflictMode;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceTableRowToUpsert {

    private final String tableName;
    /**
     * key: column name
     * value: column value
     */
    private final Map<String, Object> primaryKeyColumns = new HashMap<>();
    /**
     * key: column name
     * value: column value
     */
    private final Map<String, Object> otherColumns = new HashMap<>();
    private final AttributeWriteConflictMode writeConflictMode;

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
     * Add a primary key column of the table row to upsert.
     *
     * @param columnName    the name of the primary key column.
     * @param value         the value of the primary key column.
     * @return  the updated table row.
     */
    public PersistenceTableRowToUpsert addPrimaryKeyColumn(final String columnName, final String value) {
        primaryKeyColumns.put(columnName, value);
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
