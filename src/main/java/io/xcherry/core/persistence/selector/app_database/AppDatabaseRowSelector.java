package io.xcherry.core.persistence.selector.app_database;

import io.xcherry.core.encoder.base.DatabaseStringEncoder;
import io.xcherry.gen.models.AppDatabaseColumnValue;
import io.xcherry.gen.models.AppDatabaseTableRowSelector;
import io.xcherry.gen.models.WriteConflictMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseRowSelector {

    private final String tableName;
    private final WriteConflictMode writeConflictMode;
    private final AppDatabasePrimaryKeySelector primaryKeySelector;
    private final List<AppDatabaseColumnValueSelector> initialWrite;

    /**
     * Create an app database row selector.
     *
     * @param tableName             table name that the row belongs to.
     * @param writeConflictMode     write conflict mode.
     * @param primaryKeySelector    primary key selector of the row.
     * @param columnsToWrite        a list of {@link AppDatabaseColumnValueSelector} to be used for the initial write to the row.
     * @return  the created app database row selector.
     */
    public static AppDatabaseRowSelector create(
        final String tableName,
        final WriteConflictMode writeConflictMode,
        final AppDatabasePrimaryKeySelector primaryKeySelector,
        final AppDatabaseColumnValueSelector... columnsToWrite
    ) {
        return new AppDatabaseRowSelector(
            tableName,
            writeConflictMode,
            primaryKeySelector,
            Arrays.stream(columnsToWrite).collect(Collectors.toList())
        );
    }

    public AppDatabaseTableRowSelector toApiModel(final DatabaseStringEncoder encoder) {
        return new AppDatabaseTableRowSelector()
            .primaryKey(primaryKeySelector.toApiModel(encoder))
            .initialWrite(toApiModel(initialWrite, encoder))
            .conflictMode(writeConflictMode);
    }

    private List<AppDatabaseColumnValue> toApiModel(
        final List<AppDatabaseColumnValueSelector> selectors,
        final DatabaseStringEncoder encoder
    ) {
        return selectors.stream().map(selector -> selector.toApiModel(encoder)).collect(Collectors.toList());
    }
}
