package io.xcherry.core.persistence.selector.app_database;

import io.xcherry.core.encoder.base.DatabaseStringEncoder;
import io.xcherry.gen.models.AppDatabaseColumnValue;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabasePrimaryKeySelector {

    private final List<AppDatabaseColumnValueSelector> columnValueSelectors;

    /**
     * Create an app database primary key selector.
     *
     * @param columnValueSelectors  a list of {@link AppDatabaseColumnValueSelector}.
     * @return  the created app database primary key selector.
     */
    public static AppDatabasePrimaryKeySelector create(final AppDatabaseColumnValueSelector... columnValueSelectors) {
        return new AppDatabasePrimaryKeySelector(Arrays.stream(columnValueSelectors).collect(Collectors.toList()));
    }

    public List<AppDatabaseColumnValueSelector> getColumnValueSelectors() {
        return columnValueSelectors;
    }

    public List<AppDatabaseColumnValue> toApiModel(final DatabaseStringEncoder encoder) {
        return columnValueSelectors.stream().map(selector -> selector.toApiModel(encoder)).collect(Collectors.toList());
    }
}
