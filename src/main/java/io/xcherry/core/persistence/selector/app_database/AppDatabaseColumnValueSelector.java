package io.xcherry.core.persistence.selector.app_database;

import io.xcherry.core.encoder.base.DatabaseStringEncoder;
import io.xcherry.gen.models.AppDatabaseColumnValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseColumnValueSelector {

    private final String columnName;
    private final Object value;

    /**
     * Create an app database column value selector.
     *
     * @param columnName    column name.
     * @param value         column value.
     * @return  the created app database column value selector.
     */
    public static AppDatabaseColumnValueSelector create(final String columnName, final Object value) {
        return new AppDatabaseColumnValueSelector(columnName, value);
    }

    public AppDatabaseColumnValue toApiModel(final DatabaseStringEncoder encoder) {
        return new AppDatabaseColumnValue().column(columnName).queryValue(encoder.encodeToString(value));
    }
}
