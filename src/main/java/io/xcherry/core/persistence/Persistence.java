package io.xcherry.core.persistence;

import com.google.common.collect.ImmutableList;
import io.xcherry.core.encoder.base.DatabaseStringEncoder;
import io.xcherry.core.exception.persistence.AppDatabaseNotFoundException;
import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.gen.models.AppDatabaseColumnValue;
import io.xcherry.gen.models.AppDatabaseReadResponse;
import io.xcherry.gen.models.AppDatabaseRowReadResponse;
import io.xcherry.gen.models.AppDatabaseRowWrite;
import io.xcherry.gen.models.AppDatabaseTableReadResponse;
import io.xcherry.gen.models.AppDatabaseTableWrite;
import io.xcherry.gen.models.AppDatabaseWrite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Persistence {

    /**
     * table: rows
     */
    private final Map<String, List<AppDatabaseRow>> appDatabase = new HashMap<>();

    public Persistence(
        final AppDatabaseReadResponse appDatabaseReadResponse,
        final PersistenceSchema persistenceSchema,
        final DatabaseStringEncoder encoder
    ) {
        if (appDatabaseReadResponse == null) {
            return;
        }

        final List<AppDatabaseTableReadResponse> tables = appDatabaseReadResponse.getTables() == null
            ? ImmutableList.of()
            : appDatabaseReadResponse.getTables();

        for (final AppDatabaseTableReadResponse table : tables) {
            if (!appDatabase.containsKey(table.getTableName())) {
                appDatabase.put(table.getTableName(), new ArrayList<>());
            }

            final List<AppDatabaseRowReadResponse> rows = table.getRows() == null
                ? ImmutableList.of()
                : table.getRows();

            for (final AppDatabaseRowReadResponse row : rows) {
                final Map<String, Object> primaryKeyColumnMap = new HashMap<>();
                final Map<String, Object> otherColumnMap = new HashMap<>();

                final List<AppDatabaseColumnValue> columns = row.getColumns() == null
                    ? ImmutableList.of()
                    : row.getColumns();

                for (final AppDatabaseColumnValue column : columns) {
                    final Class<?> columnValueType = persistenceSchema.getAppDatabaseColumnValueType(
                        table.getTableName(),
                        column.getColumn()
                    );

                    if (persistenceSchema.isAppDatabasePrimaryKeyColumn(table.getTableName(), column.getColumn())) {
                        primaryKeyColumnMap.put(
                            column.getColumn(),
                            encoder.decodeFromString(column.getQueryValue(), columnValueType)
                        );
                    } else {
                        otherColumnMap.put(
                            column.getColumn(),
                            encoder.decodeFromString(column.getQueryValue(), columnValueType)
                        );
                    }
                }

                appDatabase
                    .get(table.getTableName())
                    .add(AppDatabaseRow.create(table.getTableName(), primaryKeyColumnMap, otherColumnMap));
            }
        }
    }

    /**
     * Get rows from the specified app database table.
     *
     * @param tableName table name.
     * @return  rows of the table.
     */
    public List<AppDatabaseRow> getAppDatabaseRows(final String tableName) {
        if (!appDatabase.containsKey(tableName)) {
            throw new AppDatabaseNotFoundException(String.format("Table %s does not exist in app database", tableName));
        }

        return appDatabase.get(tableName);
    }

    public AppDatabaseWrite getAppDatabaseWrite(final DatabaseStringEncoder encoder) {
        final ArrayList<AppDatabaseTableWrite> tableWrites = new ArrayList<>();

        appDatabase.forEach((table, rows) -> {
            final List<AppDatabaseRowWrite> rowWrites = rows
                .stream()
                .map(row -> row.getAppDatabaseRowWrite(encoder))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (rowWrites.isEmpty()) {
                return;
            }

            tableWrites.add(new AppDatabaseTableWrite().tableName(table).rows(rowWrites));
        });

        if (tableWrites.isEmpty()) {
            return null;
        }

        return new AppDatabaseWrite().tables(tableWrites);
    }
}
