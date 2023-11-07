package io.xdb.core.command;

import com.google.common.collect.ImmutableList;
import io.xdb.core.command.result.LocalQueueResult;
import io.xdb.core.command.result.TimerResult;
import io.xdb.core.communication.LocalQueueDef;
import io.xdb.core.encoder.ObjectEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CommandResults {

    private final List<TimerResult> timerResults;
    private final List<LocalQueueResult> localQueueResults;

    public static CommandResults fromApiModel(
        final io.xdb.gen.models.CommandResults commandResults,
        final Map<String, LocalQueueDef> localQueueDefMap,
        final ObjectEncoder objectEncoder
    ) {
        final List<TimerResult> timerResults;
        final List<LocalQueueResult> localQueueResults;

        if (commandResults.getTimerResults() != null) {
            timerResults =
                commandResults.getTimerResults().stream().map(TimerResult::fromApiModel).collect(Collectors.toList());
        } else {
            timerResults = ImmutableList.of();
        }

        if (commandResults.getLocalQueueResults() != null) {
            localQueueResults =
                commandResults
                    .getLocalQueueResults()
                    .stream()
                    .map(r -> LocalQueueResult.fromApiModel(r, localQueueDefMap.get(r.getQueueName()), objectEncoder))
                    .collect(Collectors.toList());
        } else {
            localQueueResults = ImmutableList.of();
        }

        return CommandResults.builder().timerResults(timerResults).localQueueResults(localQueueResults).build();
    }
}
