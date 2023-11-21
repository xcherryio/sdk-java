package io.xcherry.core.command.result;

import io.xcherry.gen.models.CommandStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class TimerResult {

    private final CommandStatus status;

    public static TimerResult fromApiModel(final io.xcherry.gen.models.TimerResult timerResult) {
        return TimerResult.builder().status(timerResult.getStatus()).build();
    }
}
