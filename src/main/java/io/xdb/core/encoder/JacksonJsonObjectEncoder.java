package io.xdb.core.encoder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import io.xdb.core.exception.ObjectEncoderException;
import io.xdb.gen.models.EncodedObject;

public class JacksonJsonObjectEncoder implements ObjectEncoder {

    private final ObjectMapper objectMapper;

    public JacksonJsonObjectEncoder() {
        this.objectMapper = new ObjectMapper();
        // preserve the original value of timezone coming from the server in Payload
        // without adjusting to the host timezone
        // may be important if the replay is happening on a host in another timezone
        this.objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public String getEncodingType() {
        return "BuiltinJacksonJson";
    }

    @Override
    public EncodedObject encode(final Object object) {
        if (object == null) {
            return null;
        }

        final String data;
        try {
            data = objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new ObjectEncoderException(e);
        }

        return new EncodedObject().encoding(getEncodingType()).data(data);
    }

    @Override
    public <T> T decode(final EncodedObject encodedObject, final Class<T> type) {
        if (encodedObject == null) {
            return null;
        }

        final String data = encodedObject.getData();
        if (Strings.isNullOrEmpty(data)) {
            return null;
        }

        try {
            final JavaType reference = objectMapper.getTypeFactory().constructType(type, type);
            return objectMapper.readValue(data, reference);
        } catch (final Exception e) {
            throw new ObjectEncoderException(e);
        }
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }
}
