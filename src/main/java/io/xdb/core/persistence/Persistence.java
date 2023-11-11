package io.xdb.core.persistence;

import com.google.common.collect.ImmutableList;
import io.xdb.core.encoder.ObjectEncoder;
import io.xdb.core.exception.GlobalAttributeNotFoundException;
import io.xdb.core.persistence.schema.PersistenceSchema;
import io.xdb.gen.models.GlobalAttributeTableRowUpdate;
import io.xdb.gen.models.LoadGlobalAttributeResponse;
import io.xdb.gen.models.TableColumnValue;
import io.xdb.gen.models.TableReadResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Persistence {

    /**
     * table: { columnName: columnValue }
     */
    private final Map<String, Map<String, Object>> globalAttributes = new HashMap<>();
    /**
     * table: { columnName: columnValue }
     */
    private final Map<String, Map<String, Object>> globalAttributesToUpdate = new HashMap<>();

    private final ObjectEncoder objectEncoder;

    public Persistence(
        final LoadGlobalAttributeResponse loadGlobalAttributeResponse,
        final PersistenceSchema persistenceSchema,
        final ObjectEncoder objectEncoder
    ) {
        this.objectEncoder = objectEncoder;

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
                final Class<?> columnValueType = persistenceSchema.getGlobalAttributeColumnValueType(
                    tableResponse.getTableName(),
                    column.getDbColumn()
                );

                globalAttributes
                    .get(tableResponse.getTableName())
                    .put(
                        column.getDbColumn(),
                        objectEncoder.decodeFromString(column.getDbQueryValue(), columnValueType)
                    );
            }
        }
    }

    /**
     * Get a global attribute as the column value from the specified table column.
     *
     * @param tableName     the table where the global attribute is stored.
     * @param columnName    the column of the table where the global attribute is stored.
     * @return  the column value as the global attribute.
     */
    public Object getGlobalAttribute(final String tableName, final String columnName) {
        if (!globalAttributes.containsKey(tableName)) {
            throw new GlobalAttributeNotFoundException(
                String.format("Table %s does not exist within the global attributes", tableName)
            );
        }
        if (!globalAttributes.get(tableName).containsKey(columnName)) {
            throw new GlobalAttributeNotFoundException(
                String.format(
                    "Column %s does not exist in the table %s within the global attributes",
                    columnName,
                    tableName
                )
            );
        }

        return globalAttributes.get(tableName).get(columnName);
    }

    /**
     * Upsert a global attribute.
     * If a global attribute already exists in the column, update its value. Otherwise, insert a new row with the column value as the global attribute.
     *
     * @param tableName     the table where the global attribute will be stored.
     * @param columnName    the column of the table where the global attribute will be stored.
     * @param columnValue   the column value to upsert as the global attribute.
     */
    public void upsertGlobalAttribute(final String tableName, final String columnName, final Object columnValue) {
        if (!globalAttributesToUpdate.containsKey(tableName)) {
            globalAttributesToUpdate.put(tableName, new HashMap<>());
        }

        globalAttributesToUpdate.get(tableName).put(columnName, columnValue);
    }

    public List<GlobalAttributeTableRowUpdate> getGlobalAttributesToUpsert() {
        final List<GlobalAttributeTableRowUpdate> globalAttributes = new ArrayList<>();

        globalAttributesToUpdate.forEach((table, columnsToUpdate) -> {
            final List<TableColumnValue> columns = new ArrayList<>();

            columnsToUpdate.forEach((key, value) -> {
                columns.add(new TableColumnValue().dbColumn(key).dbQueryValue(objectEncoder.encodeToString(value)));
            });

            globalAttributes.add(new GlobalAttributeTableRowUpdate().tableName(table).updateColumns(columns));
        });

        return globalAttributes;
    }
}
