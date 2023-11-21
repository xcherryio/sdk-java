package io.xcherry.core.command.result;

import com.google.common.collect.ImmutableList;
import io.xcherry.core.encoder.base.ObjectEncoder;
import io.xcherry.gen.models.CommandStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class LocalQueueResult {

    private final CommandStatus status;
    private final String queueName;
    private final List<LocalQueueMessageResult> messages;

    public static LocalQueueResult fromApiModel(
        final io.xcherry.gen.models.LocalQueueResult localQueueResult,
        final ObjectEncoder objectEncoder
    ) {
        final List<LocalQueueMessageResult> localQueueMessageResults;

        if (localQueueResult.getMessages() != null) {
            localQueueMessageResults =
                localQueueResult
                    .getMessages()
                    .stream()
                    .map(m -> LocalQueueMessageResult.fromApiModel(m, objectEncoder))
                    .collect(Collectors.toList());
        } else {
            localQueueMessageResults = ImmutableList.of();
        }

        return LocalQueueResult
            .builder()
            .status(localQueueResult.getStatus())
            .queueName(localQueueResult.getQueueName())
            .messages(localQueueMessageResults)
            .build();
    }
}
