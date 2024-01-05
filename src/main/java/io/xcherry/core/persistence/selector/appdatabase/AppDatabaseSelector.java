package io.xcherry.core.persistence.selector.appdatabase;

import io.xcherry.core.encoder.base.DatabaseStringEncoder;
import io.xcherry.gen.models.AppDatabaseTableConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseSelector {

    /**
     * table: rows
     */
    private final Map<String, List<AppDatabaseRowSelector>> tableToRowsMap;

    /**
     * Create an app database selector.
     *
     * @param rowSelectors  a list of {@link AppDatabaseRowSelector}.
     * @return the created app database selector.
     */
    public static AppDatabaseSelector create(final AppDatabaseRowSelector... rowSelectors) {
        final Map<String, List<AppDatabaseRowSelector>> tableToRowsMap = new HashMap<>();

        for (final AppDatabaseRowSelector rowSelector : rowSelectors) {
            if (!tableToRowsMap.containsKey(rowSelector.getTableName())) {
                tableToRowsMap.put(rowSelector.getTableName(), new ArrayList<>());
            }

            tableToRowsMap.get(rowSelector.getTableName()).add(rowSelector);
        }

        return new AppDatabaseSelector(tableToRowsMap);
    }

    public Map<String, List<AppDatabaseRowSelector>> getTableToRowsMap() {
        return tableToRowsMap;
    }

    public io.xcherry.gen.models.AppDatabaseConfig toApiModel(final DatabaseStringEncoder encoder) {
        if (tableToRowsMap.isEmpty()) {
            return null;
        }

        final io.xcherry.gen.models.AppDatabaseConfig appDatabaseConfig = new io.xcherry.gen.models.AppDatabaseConfig();

        tableToRowsMap.forEach((tableName, rows) -> {
            appDatabaseConfig.addTablesItem(
                new AppDatabaseTableConfig()
                    .tableName(tableName)
                    .rows(rows.stream().map(row -> row.toApiModel(encoder)).collect(Collectors.toList()))
            );
        });

        return appDatabaseConfig;
    }
}
