package io.xdb.core.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.xdb.gen.models.EncodedObject;

public interface ObjectEncoder {
    /**
     * Each {@link ObjectEncoder} has an Encoding Type that it handles.
     *
     * @return encoding type that this converter handles.
     */
    String getEncodingType();

    /**
     * Encode a Java object to an EncodedObject
     *
     * @param object Java object to convert
     * @return encoded object with the encoding type of the encoder
     */
    EncodedObject encode(final Object object);

    /**
     * Decode an encoded object into a Java object with the Encoding Type
     * @param encodedObject encoded object to decode
     * @param type Java class to decode into
     * @param <T> Java class to decode into
     * @return decoded Java object
     */
    <T> T decode(final EncodedObject encodedObject, final Class<T> type);

    ObjectMapper getObjectMapper();
}
