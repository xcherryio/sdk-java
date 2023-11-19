package io.xdb.core.communication;

import io.xdb.core.encoder.base.ObjectEncoder;
import io.xdb.core.state.StateDecision;
import io.xdb.gen.models.LocalQueueMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Communication {

    private final ObjectEncoder objectEncoder;
    private final List<LocalQueueMessage> localQueueMessagesToPublish = new ArrayList<>();
    /**
     * only used for RPC
     */
    private StateDecision stateDecision = null;

    /**
     * Publish message(s) to the local queue for consumption by the process execution.
     *
     * @param queueNames     queue names for categorizing message types.
     */
    public void publishToLocalQueue(final String... queueNames) {
        for (final String queueName : queueNames) {
            publishToLocalQueue(queueName, null);
        }
    }

    /**
     * Publish a message to the local queue for consumption by the process execution.
     *
     * @param queueName     queue name for categorizing message types.
     * @param payload       payload of the message.
     */
    public void publishToLocalQueue(final String queueName, final Object payload) {
        publishToLocalQueue(queueName, "", payload);
    }

    /**
     * Publish a message to the local queue for consumption by the process execution.
     *
     * @param queueName     queue name for categorizing message types.
     * @param dedupId       UUID to uniquely distinguish different messages. If not specified, the server will generate a UUID instead.
     * @param payload       payload of the message.
     */
    public void publishToLocalQueue(final String queueName, final String dedupId, final Object payload) {
        final LocalQueueMessage message = new LocalQueueMessage()
            .queueName(queueName)
            .dedupId(dedupId)
            .payload(objectEncoder.encodeToEncodedObject(payload));

        localQueueMessagesToPublish.add(message);
    }

    /**
     * Trigger new state decision from @RPC methods.
     *
     * @param stateDecision     the state decision to trigger.
     */
    public void triggerNewStateDecision(final StateDecision stateDecision) {
        this.stateDecision = stateDecision;
    }
}
