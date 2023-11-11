package io.xdb.core.persistence.schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceTableColumnSchema {

    private final String columnName;
    private final Class<?> valueType;
    private final boolean loadByDefault;

    public static PersistenceTableColumnSchema create(
        final String columnName,
        final Class<?> valueType,
        final boolean loadByDefault
    ) {
        return new PersistenceTableColumnSchema(columnName, valueType, loadByDefault);
    }
}
