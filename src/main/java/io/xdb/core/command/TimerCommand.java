package io.xdb.core.command;

import lombok.Getter;

@Getter
public class TimerCommand implements BaseCommand {

    private long delayInSeconds;

    public TimerCommand(final long delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }
}
