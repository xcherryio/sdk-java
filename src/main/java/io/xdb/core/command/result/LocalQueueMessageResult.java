package io.xdb.core.command.result;

import io.xdb.core.encoder.ObjectEncoder;
import io.xdb.gen.models.EncodedObject;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class LocalQueueMessageResult<T> {

    private final String dedupId;
    private final EncodedObject payload;
    private final ObjectEncoder objectEncoder;

    public static LocalQueueMessageResult fromApiModel(
        final io.xdb.gen.models.LocalQueueMessageResult localQueueMessageResult,
        final ObjectEncoder objectEncoder
    ) {
        return LocalQueueMessageResult
            .builder()
            .dedupId(localQueueMessageResult.getDedupId())
            .payload(localQueueMessageResult.getPayload())
            .objectEncoder(objectEncoder)
            .build();
    }

    public String getDedupId() {
        return dedupId;
    }

    public T getPayload(final Class<T> tClass) {
        return objectEncoder.decode(payload, tClass);
    }
}
