package io.xdb.core.persistence;

import com.google.common.collect.ImmutableList;
import io.xdb.core.exception.GlobalAttributeNotFoundException;
import io.xdb.gen.models.GlobalAttributeTableRowUpdate;
import io.xdb.gen.models.LoadGlobalAttributeResponse;
import io.xdb.gen.models.TableColumnValue;
import io.xdb.gen.models.TableReadResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Persistence {

    // table: { columnKey: columnValue }
    private final Map<String, Map<String, String>> globalAttributes = new HashMap<>();
    // table: { columnKey: columnValue }
    private final Map<String, Map<String, String>> globalAttributesToUpdate = new HashMap<>();

    public Persistence(final LoadGlobalAttributeResponse loadGlobalAttributeResponse) {
        if (loadGlobalAttributeResponse == null) {
            return;
        }

        final List<TableReadResponse> tableResponses = loadGlobalAttributeResponse.getTableResponses() == null
            ? ImmutableList.of()
            : loadGlobalAttributeResponse.getTableResponses();
        for (final TableReadResponse tableResponse : tableResponses) {
            if (!globalAttributes.containsKey(tableResponse.getTableName())) {
                globalAttributes.put(tableResponse.getTableName(), new HashMap<>());
            }

            final List<TableColumnValue> columns = tableResponse.getColumns() == null
                ? ImmutableList.of()
                : tableResponse.getColumns();
            for (final TableColumnValue column : columns) {
                globalAttributes.get(tableResponse.getTableName()).put(column.getDbColumn(), column.getDbQueryValue());
            }
        }
    }

    /**
     * Get a global attribute.
     *
     * @param table
     * @param columnKey
     * @return  column value.
     */
    public String getGlobalAttribute(final String table, final String columnKey) {
        if (!globalAttributes.containsKey(table)) {
            throw new GlobalAttributeNotFoundException(
                String.format("Table %s does not exist within the global attributes", table)
            );
        }
        if (!globalAttributes.get(table).containsKey(columnKey)) {
            throw new GlobalAttributeNotFoundException(
                String.format(
                    "Column key %s does not exist in the table %s within the global attributes",
                    columnKey,
                    table
                )
            );
        }
        return globalAttributes.get(table).get(columnKey);
    }

    /**
     * Upsert a global attribute. If the table and column key exists, update the value. Else, insert a new global attribute.
     *
     * @param table
     * @param columnKey
     * @param columnValue
     */
    public void upsertGlobalAttribute(final String table, final String columnKey, final String columnValue) {
        if (!globalAttributesToUpdate.containsKey(table)) {
            globalAttributesToUpdate.put(table, new HashMap<>());
        }

        globalAttributesToUpdate.get(table).put(columnKey, columnValue);
    }

    public List<GlobalAttributeTableRowUpdate> getGlobalAttributesToUpsert() {
        final List<GlobalAttributeTableRowUpdate> globalAttributes = new ArrayList<>();

        globalAttributesToUpdate.forEach((table, columnsToUpdate) -> {
            final List<TableColumnValue> columns = new ArrayList<>();

            columnsToUpdate.forEach((key, value) -> {
                columns.add(new TableColumnValue().dbColumn(key).dbQueryValue(value));
            });

            globalAttributes.add(new GlobalAttributeTableRowUpdate().tableName(table).updateColumns(columns));
        });

        return globalAttributes;
    }
}
