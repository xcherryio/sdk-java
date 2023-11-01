package io.xdb.core.command;

import lombok.Getter;

@Getter
public class LocalQueueCommand implements BaseCommand {

    private final String queueName;
    private final int count;

    public LocalQueueCommand(final String queueName) {
        this(queueName, 1);
    }

    public LocalQueueCommand(final String queueName, final int count) {
        this.queueName = queueName;
        this.count = count;
    }
}
