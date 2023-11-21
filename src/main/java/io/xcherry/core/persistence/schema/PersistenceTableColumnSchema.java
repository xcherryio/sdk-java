package io.xcherry.core.persistence.schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceTableColumnSchema {

    private final String columnName;
    private final Class<?> valueType;
    private final boolean isPrimaryKey;
    private final boolean loadByDefault;

    public static PersistenceTableColumnSchema create(
        final String columnName,
        final Class<?> valueType,
        final boolean isPrimaryKey,
        final boolean loadByDefault
    ) {
        return new PersistenceTableColumnSchema(columnName, valueType, isPrimaryKey, loadByDefault);
    }
}
