package io.xcherry.core.persistence;

import io.xcherry.core.encoder.base.DatabaseStringEncoder;
import io.xcherry.core.exception.persistence.AppDatabaseNotFoundException;
import io.xcherry.gen.models.AppDatabaseColumnValue;
import io.xcherry.gen.models.AppDatabaseRowWrite;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseRow {

    private final String tableName;

    /**
     * columnName: value
     */
    private final Map<String, Object> primaryKeyColumnMap;
    /**
     * columnName: value
     */
    private final Map<String, Object> otherColumnMap;
    /**
     * columnName: value
     */
    private final Map<String, Object> columnMapToWrite = new HashMap<>();

    /**
     * Create an app database row.
     *
     * @param primaryKeyColumnMap   the column name to value map of primary key columns.
     * @param otherColumnMap        the column name to value map of non-primary-key columns.
     * @return  an app database row.
     */
    public static AppDatabaseRow create(
        final String tableName,
        final Map<String, Object> primaryKeyColumnMap,
        final Map<String, Object> otherColumnMap
    ) {
        return new AppDatabaseRow(tableName, primaryKeyColumnMap, otherColumnMap);
    }

    /**
     * Return the value of the specified column in this row.
     *
     * @param columnName    column name.
     * @return  the column value.
     */
    public Object getColumnValue(final String columnName) {
        if (primaryKeyColumnMap.containsKey(columnName)) {
            return primaryKeyColumnMap.get(columnName);
        }

        if (otherColumnMap.containsKey(columnName)) {
            return otherColumnMap.get(columnName);
        }

        throw new AppDatabaseNotFoundException(
            String.format("Column %s does not exist in the table %s in app database", columnName, tableName)
        );
    }

    /**
     * Update or insert value for the column.
     *
     * @param columnName    column name.
     * @param value         column value.
     */
    public void upsertColumn(final String columnName, final Object value) {
        columnMapToWrite.put(columnName, value);
    }

    public AppDatabaseRowWrite getAppDatabaseRowWrite(final DatabaseStringEncoder encoder) {
        if (columnMapToWrite.isEmpty()) {
            return null;
        }

        final AppDatabaseRowWrite rowWrite = new AppDatabaseRowWrite();

        primaryKeyColumnMap.forEach((columnName, value) -> {
            rowWrite.addPrimaryKeyItem(
                new AppDatabaseColumnValue().column(columnName).queryValue(encoder.encodeToString(value))
            );
        });

        columnMapToWrite.forEach((columnName, value) -> {
            rowWrite.addWriteColumnsItem(
                new AppDatabaseColumnValue().column(columnName).queryValue(encoder.encodeToString(value))
            );
        });

        return rowWrite;
    }
}
