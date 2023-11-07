package io.xdb.core.command.result;

import io.xdb.core.communication.LocalQueueDef;
import io.xdb.core.encoder.ObjectEncoder;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class LocalQueueMessageResult {

    private final String dedupId;
    private final Object payload;

    public static LocalQueueMessageResult fromApiModel(
        final io.xdb.gen.models.LocalQueueMessageResult localQueueMessageResult,
        final LocalQueueDef localQueueDef,
        final ObjectEncoder objectEncoder
    ) {
        return LocalQueueMessageResult
            .builder()
            .dedupId(localQueueMessageResult.getDedupId())
            .payload(objectEncoder.decode(localQueueMessageResult.getPayload(), localQueueDef.getPayloadClass()))
            .build();
    }
}
