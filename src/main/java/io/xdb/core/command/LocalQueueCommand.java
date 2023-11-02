package io.xdb.core.command;

import lombok.Getter;

@Getter
public class LocalQueueCommand implements BaseCommand {

    private final String queueName;
    private final int count;

    private LocalQueueCommand(final String queueName, final int count) {
        this.queueName = queueName;
        this.count = count;
    }

    /**
     * Create a local queue command with queueName, and set the default waiting message count to 1.
     *
     * @param queueName the name of the local queue.
     * @return  local queue command
     */
    public static LocalQueueCommand create(final String queueName) {
        return LocalQueueCommand.create(queueName, 1);
    }

    /**
     * Create a local queue command with queueName, and waiting message count.
     *
     * @param queueName the name of the local queue.
     * @param count the count of messages of the queue to wait for.
     * @return local queue command
     */
    public static LocalQueueCommand create(final String queueName, final int count) {
        return new LocalQueueCommand(queueName, count);
    }
}
