package io.xdb.core.communication;

import lombok.Getter;

@Getter
public class LocalQueueDef {

    /**
     * local queue name.
     */
    private final String queueName;
    /**
     * the class used to decode the encoded payload.
     */
    private final Class payloadClass;

    private LocalQueueDef(final String queueName, final Class payloadClass) {
        this.queueName = queueName;
        this.payloadClass = payloadClass;
    }

    public static LocalQueueDef create(final String queueName, final Class payloadClass) {
        return new LocalQueueDef(queueName, payloadClass);
    }
}
