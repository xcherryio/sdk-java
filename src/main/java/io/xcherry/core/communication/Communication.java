package io.xcherry.core.communication;

import io.xcherry.core.encoder.base.ObjectEncoder;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.StateDecision;
import io.xcherry.core.state.StateMovement;
import io.xcherry.gen.models.LocalQueueMessage;
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
     * Trigger a single state movement from an @RPC method.
     *
     * @param stateClass    the class of the target state to trigger.
     * @param stateInput    the input.
     */
    public void triggerSingleStateMovement(final Class<? extends AsyncState> stateClass, final Object stateInput) {
        this.stateDecision = StateDecision.singleNextState(stateClass, stateInput);
    }

    /**
     * Trigger a single state movement from an @RPC method.
     *
     * @param stateId       the id of the target state to trigger.
     * @param stateInput    the input.
     */
    public void triggerSingleStateMovement(final String stateId, final Object stateInput) {
        this.stateDecision = StateDecision.singleNextState(stateId, stateInput);
    }

    /**
     * Trigger multiple state movements from an @RPC method.
     *
     * @param stateClasses  the classes of the target states to trigger.
     */
    public void triggerMultipleStateMovements(final Class<? extends AsyncState>... stateClasses) {
        this.stateDecision = StateDecision.multipleNextStates(stateClasses);
    }

    /**
     * Trigger multiple state movements from an @RPC method.
     *
     * @param stateIds  the ids of the target states to trigger.
     */
    public void triggerMultipleStateMovements(final String... stateIds) {
        this.stateDecision = StateDecision.multipleNextStates(stateIds);
    }

    /**
     * Trigger multiple state movements from an @RPC method.
     *
     * @param stateMovements  the state movements to trigger.
     */
    public void triggerMultipleStateMovements(final StateMovement... stateMovements) {
        this.stateDecision = StateDecision.multipleNextStates(stateMovements);
    }
}
