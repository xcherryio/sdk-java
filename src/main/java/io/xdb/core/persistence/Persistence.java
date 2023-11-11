package io.xdb.core.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.xdb.core.exception.global_attribute.GlobalAttributeDecodeException;
import io.xdb.core.exception.global_attribute.GlobalAttributeEncodeException;
import io.xdb.core.exception.global_attribute.GlobalAttributeNotFoundException;
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
    private final Map<String, Map<String, Object>> globalAttributes = new HashMap<>();
    // table: { columnKey: columnValue }
    private final Map<String, Map<String, Object>> globalAttributesToUpdate = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Persistence(
        final LoadGlobalAttributeResponse loadGlobalAttributeResponse,
        final PersistenceSchema persistenceSchema
    ) {
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

                final Object columnValue;
                try {
                    columnValue = objectMapper.readValue(column.getDbQueryValue(), columnValueType);
                } catch (final Exception e) {
                    throw new GlobalAttributeDecodeException(
                        String.format(
                            "Failed to decode the global attribute column value %s of column %s with the type %s",
                            column.getDbQueryValue(),
                            column.getDbColumn(),
                            columnValueType.getName()
                        )
                    );
                }

                globalAttributes.get(tableResponse.getTableName()).put(column.getDbColumn(), columnValue);
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

    public List<GlobalAttributeTableRowUpdate> getGlobalAttributesToUpsert(final PersistenceSchema persistenceSchema) {
        final List<GlobalAttributeTableRowUpdate> globalAttributes = new ArrayList<>();

        globalAttributesToUpdate.forEach((table, columnsToUpdate) -> {
            final List<TableColumnValue> columns = new ArrayList<>();

            columnsToUpdate.forEach((key, value) -> {
                final String stringValue;
                try {
                    stringValue = objectMapper.writeValueAsString(value);
                } catch (final Exception e) {
                    throw new GlobalAttributeEncodeException(
                        String.format(
                            "Failed to encode the global attribute column value %s of column %s to a string",
                            value,
                            key
                        )
                    );
                }

                columns.add(new TableColumnValue().dbColumn(key).dbQueryValue(stringValue));
            });

            globalAttributes.add(new GlobalAttributeTableRowUpdate().tableName(table).updateColumns(columns));
        });

        return globalAttributes;
    }
}
