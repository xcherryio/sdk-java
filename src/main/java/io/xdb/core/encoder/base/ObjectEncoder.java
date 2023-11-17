package io.xdb.core.encoder.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.xdb.gen.models.EncodedObject;

public interface ObjectEncoder {
    /**
     * @return the object mapper used to encode and decode.
     */
    ObjectMapper getObjectMapper();

    /**
     * Each {@link ObjectEncoder} has an Encoding Type that it handles.
     *
     * @return encoding type that this converter handles.
     */
    String getEncodingType();

    /**
     * Encode a Java object to an EncodedObject.
     *
     * @param object Java object to convert.
     * @return encoded object with the encoding type of the encoder.
     */
    EncodedObject encodeToEncodedObject(final Object object);

    /**
     * Decode an encoded object into a Java object with the Encoding Type.
     *
     * @param encodedObject encoded object to decode.
     * @param type Java class to decode into.
     * @param <T> Java class to decode into.
     * @return decoded Java object.
     */
    <T> T decodeFromEncodedObject(final EncodedObject encodedObject, final Class<T> type);
}
