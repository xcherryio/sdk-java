package io.xcherry.core.persistence.selector.local_attribute;

import io.xcherry.core.encoder.base.ObjectEncoder;
import io.xcherry.gen.models.KeyValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalAttributeKeyValueSelector {

    private final String key;
    private final Object value;

    /**
     * Create a local attribute key value selector.
     *
     * @param key       key.
     * @param value     value.
     * @return  the created local attribute key value selector.
     */
    public static LocalAttributeKeyValueSelector create(final String key, final Object value) {
        return new LocalAttributeKeyValueSelector(key, value);
    }

    public KeyValue toApiModel(final ObjectEncoder encoder) {
        return new KeyValue().key(key).value(encoder.encodeToEncodedObject(value));
    }
}
