package io.xdb.core.command.result;

import io.xdb.gen.models.CommandStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class TimerResult {

    private final CommandStatus status;

    public static TimerResult fromApiModel(final io.xdb.gen.models.TimerResult timerResult) {
        return TimerResult.builder().status(timerResult.getStatus()).build();
    }
}
