package io.xcherry.core.encoder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import io.xcherry.core.encoder.base.ObjectEncoder;
import io.xcherry.core.exception.ObjectEncoderException;
import io.xcherry.gen.models.EncodedObject;

public class JacksonObjectEncoder implements ObjectEncoder {

    private final ObjectMapper objectMapper;

    public JacksonObjectEncoder() {
        this.objectMapper = new ObjectMapper();
        // preserve the original value of timezone coming from the server in Payload
        // without adjusting to the host timezone
        this.objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    public String getEncodingType() {
        return "BuiltinJacksonJson";
    }

    private String encodeToString(final Object object) {
        if (object == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new ObjectEncoderException(e);
        }
    }

    private <T> T decodeFromString(final String encodedString, final Class<T> type) {
        if (Strings.isNullOrEmpty(encodedString)) {
            return null;
        }

        final JavaType reference = objectMapper.getTypeFactory().constructType(type, type);

        try {
            return objectMapper.readValue(encodedString, reference);
        } catch (final Exception e) {
            throw new ObjectEncoderException(e);
        }
    }

    @Override
    public EncodedObject encodeToEncodedObject(final Object object) {
        return new EncodedObject().encoding(getEncodingType()).data(encodeToString(object));
    }

    @Override
    public <T> T decodeFromEncodedObject(final EncodedObject encodedObject, final Class<T> type) {
        if (encodedObject == null) {
            return null;
        }

        return decodeFromString(encodedObject.getData(), type);
    }
}
