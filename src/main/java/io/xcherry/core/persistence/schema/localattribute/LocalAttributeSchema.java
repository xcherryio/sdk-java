package io.xcherry.core.persistence.schema.localattribute;

import io.xcherry.core.exception.persistence.LocalAttributeSchemaNotMatchException;
import io.xcherry.core.persistence.readrequest.LocalAttributeReadRequest;
import io.xcherry.gen.models.DatabaseLockingType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalAttributeSchema {

    private final DatabaseLockingType lockingType;
    /**
     * key: schema
     */
    private final Map<String, LocalAttributeKeySchema> keySchemaMap;

    public static LocalAttributeSchema EMPTY() {
        return LocalAttributeSchema.define();
    }

    /**
     * Define a local attribute schema.
     *
     * @param keySchemas   a list of {@link LocalAttributeKeySchema}.
     * @return the defined local attribute schema.
     */
    public static LocalAttributeSchema define(final LocalAttributeKeySchema... keySchemas) {
        final Map<String, LocalAttributeKeySchema> keySchemaMap = new HashMap<>();

        for (final LocalAttributeKeySchema keySchema : keySchemas) {
            keySchemaMap.put(keySchema.getKey(), keySchema);
        }

        return new LocalAttributeSchema(DatabaseLockingType.NO_LOCKING, keySchemaMap);
    }

    public Set<String> getKeys() {
        return keySchemaMap.keySet();
    }

    public boolean contains(final String key) {
        return keySchemaMap.containsKey(key);
    }

    public Class<?> getKeyValueType(final String key) {
        if (!contains(key)) {
            throw new LocalAttributeSchemaNotMatchException(
                String.format("Key %s is not defined in the local attribute schema", key)
            );
        }

        return keySchemaMap.get(key).getValueType();
    }

    public LocalAttributeReadRequest getReadRequest() {
        if (keySchemaMap.isEmpty()) {
            return null;
        }

        final Set<String> keysToReadNoLock = new HashSet<>();
        final Set<String> keysToReadWithLock = new HashSet<>();

        keySchemaMap.forEach((key, keySchema) -> {
            if (!keySchema.isLoadByDefault()) {
                return;
            }

            if (!keySchema.isLocking()) {
                keysToReadNoLock.add(key);
            } else {
                keysToReadWithLock.add(key);
            }
        });

        return LocalAttributeReadRequest.create(lockingType, keysToReadNoLock, keysToReadWithLock);
    }
}
