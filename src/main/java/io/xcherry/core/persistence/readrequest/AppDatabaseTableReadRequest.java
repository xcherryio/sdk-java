package io.xcherry.core.persistence.readrequest;

import io.xcherry.gen.models.LockType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseTableReadRequest {

    private final String tableName;
    private final LockType lockType;
    private final Set<String> columnNames;

    /**
     * Create an app database table read request.
     *
     * @param tableName     table name.
     * @param lockType      lock type.
     * @param columnNames   a set to column names to read from the table.
     * @return  the created app database table read request.
     */
    public static AppDatabaseTableReadRequest create(
        final String tableName,
        final LockType lockType,
        final String... columnNames
    ) {
        return AppDatabaseTableReadRequest.create(
            tableName,
            lockType,
            Arrays.stream(columnNames).collect(Collectors.toSet())
        );
    }

    /**
     * Create an app database table read request.
     *
     * @param tableName     table name.
     * @param lockType      lock type.
     * @param columnNames   a set to column names to read from the table.
     * @return  the created app database table read request.
     */
    public static AppDatabaseTableReadRequest create(
        final String tableName,
        final LockType lockType,
        final Set<String> columnNames
    ) {
        return new AppDatabaseTableReadRequest(tableName, lockType, columnNames);
    }

    public String getTableName() {
        return tableName;
    }

    public Set<String> getColumnNames() {
        return columnNames;
    }

    public io.xcherry.gen.models.AppDatabaseTableReadRequest toApiModel() {
        return new io.xcherry.gen.models.AppDatabaseTableReadRequest()
            .tableName(tableName)
            .lockType(lockType)
            .columns(new ArrayList<>(columnNames));
    }
}
