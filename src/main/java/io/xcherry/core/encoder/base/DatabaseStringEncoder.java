package io.xcherry.core.encoder.base;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface DatabaseStringEncoder {
    /**
     * @return the object mapper used to encode and decode.
     */
    ObjectMapper getObjectMapper();

    /**
     * Encode a Java object to a string.
     *
     * @param object Java object to convert.
     * @return encoded string.
     */
    String encodeToString(final Object object);

    /**
     * Decode an encoded string into a Java object with the Encoding Type.
     *
     * @param encodedString encoded string to decode.
     * @param type Java class to decode into.
     * @param <T> Java class to decode into.
     * @return decoded Java object.
     */
    <T> T decodeFromString(final String encodedString, final Class<T> type);
}
