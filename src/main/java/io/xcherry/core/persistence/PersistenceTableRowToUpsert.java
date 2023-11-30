package io.xcherry.core.persistence;

import io.xcherry.gen.models.WriteConflictMode;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceTableRowToUpsert {

    private final String tableName;
    /**
     * column name: column value
     */
    private final Map<String, Object> primaryKeyColumns;
    /**
     * column name: column value
     */
    private final Map<String, Object> otherColumns;
    private final WriteConflictMode writeConflictMode;

    /**
     * Create a table row to upsert.
     *
     * @param tableName             the name of the table.
     * @param primaryKeyColumns     the column names and values of primary key.
     * @param otherColumns          the column names and values of non-primary key.
     * @param writeConflictMode     the mode to choose when there is a conflict in upsert.
     * @return  the table row to upsert.
     */
    public static PersistenceTableRowToUpsert create(
        final String tableName,
        final Map<String, Object> primaryKeyColumns,
        final Map<String, Object> otherColumns,
        final WriteConflictMode writeConflictMode
    ) {
        return new PersistenceTableRowToUpsert(tableName, primaryKeyColumns, otherColumns, writeConflictMode);
    }
}
