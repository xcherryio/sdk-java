package io.xcherry.core.persistence.schema.appdatabase;

import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.persistence.Persistence;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseColumnSchema {

    private final String columnName;
    private final Class<?> valueType;
    private final boolean loadByDefault;

    /**
     * Define an app database column schema.
     *
     * @param columnName        column name.
     * @param valueType         value type.
     * @return  the defined app database column schema.
     */
    public static AppDatabaseColumnSchema define(final String columnName, final Class<?> valueType) {
        return AppDatabaseColumnSchema.define(columnName, valueType, true);
    }

    /**
     * Define an app database column schema.
     *
     * @param columnName        column name.
     * @param valueType         value type.
     * @param loadByDefault     set true to load this column by default in the {@link io.xcherry.core.state.AsyncState#execute(Context, Object, CommandResults, Persistence, Communication)} and {@link io.xcherry.core.rpc.RPC}.
     * @return  the defined app database column schema.
     */
    public static AppDatabaseColumnSchema define(
        final String columnName,
        final Class<?> valueType,
        final boolean loadByDefault
    ) {
        return new AppDatabaseColumnSchema(columnName, valueType, loadByDefault);
    }
}
