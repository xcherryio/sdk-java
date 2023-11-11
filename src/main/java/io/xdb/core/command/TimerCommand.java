package io.xdb.core.command;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimerCommand implements BaseCommand {

    private long delayInSeconds;

    /**
     * Set a timer to be fired after the specified duration.
     *
     * @param duration
     * @return
     */
    public static TimerCommand byDuration(final Duration duration) {
        return new TimerCommand(duration.getSeconds());
    }

    /**
     * Set a timer to be fired at the specified dateTime.
     *
     * @param dateTime
     * @return
     */
    public static TimerCommand byFireTime(final LocalDateTime dateTime) {
        return TimerCommand.byDuration(Duration.between(LocalDateTime.now(), dateTime));
    }
}
