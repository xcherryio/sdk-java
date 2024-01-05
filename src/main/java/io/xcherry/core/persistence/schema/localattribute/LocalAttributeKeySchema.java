package io.xcherry.core.persistence.schema.localattribute;

import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.persistence.Persistence;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalAttributeKeySchema {

    private final String key;
    private final Class<?> valueType;
    private final boolean loadByDefault;
    private final boolean isLocking;

    /**
     * Define a local attribute key schema.
     *
     * @param key               key name.
     * @param valueType         value type.
     * @return  the defined local attribute key schema.
     */
    public static LocalAttributeKeySchema define(final String key, final Class<?> valueType) {
        return LocalAttributeKeySchema.define(key, valueType, true, false);
    }

    /**
     * Define a local attribute key schema.
     *
     * @param key               key name.
     * @param valueType         value type.
     * @param loadByDefault     set true to load this local attribute by default in the {@link io.xcherry.core.state.AsyncState#execute(Context, Object, CommandResults, Persistence, Communication)} and {@link io.xcherry.core.rpc.RPC}.
     * @param isLocking         set true to load this local attribute with {@link io.xcherry.gen.models.DatabaseLockingType}.
     * @return  the defined local attribute key schema.
     */
    public static LocalAttributeKeySchema define(
        final String key,
        final Class<?> valueType,
        final boolean loadByDefault,
        final boolean isLocking
    ) {
        return new LocalAttributeKeySchema(key, valueType, loadByDefault, isLocking);
    }
}
