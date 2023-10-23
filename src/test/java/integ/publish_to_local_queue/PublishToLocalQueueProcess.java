package integ.publish_to_local_queue;

import static integ.publish_to_local_queue.TestPublishToLocalQueueProcess.DEDUP_ID;
import static integ.publish_to_local_queue.TestPublishToLocalQueueProcess.PAYLOAD_1;
import static integ.publish_to_local_queue.TestPublishToLocalQueueProcess.PAYLOAD_1_2;
import static integ.publish_to_local_queue.TestPublishToLocalQueueProcess.PAYLOAD_2;
import static integ.publish_to_local_queue.TestPublishToLocalQueueProcess.QUEUE_1;
import static integ.publish_to_local_queue.TestPublishToLocalQueueProcess.QUEUE_2;
import static integ.publish_to_local_queue.TestPublishToLocalQueueProcess.QUEUE_3;

import com.google.common.collect.ImmutableList;
import io.xdb.core.communication.Communication;
import io.xdb.core.encoder.JacksonJsonObjectEncoder;
import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateDecision;
import io.xdb.core.state.StateSchema;
import io.xdb.gen.models.AsyncStateExecuteRequest;
import io.xdb.gen.models.CommandRequest;
import io.xdb.gen.models.CommandWaitingType;
import io.xdb.gen.models.LocalQueueCommand;
import io.xdb.gen.models.LocalQueueMessage;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

@Component
public class PublishToLocalQueueProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new PublishToLocalQueueStartingState(), new PublishToLocalQueueState1());
    }
}

class PublishToLocalQueueStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest waitUntil(final Void input, final Communication communication) {
        System.out.println("PublishToLocalQueueStartingState.waitUntil: " + input);

        // will be consumed by PublishToLocalQueueState1
        communication.publishToLocalQueue(QUEUE_2, PAYLOAD_2);

        return new CommandRequest()
            .waitingType(CommandWaitingType.ANYOFCOMPLETION)
            .localQueueCommands(
                ImmutableList.of(new LocalQueueCommand().queueName(QUEUE_1), new LocalQueueCommand().queueName(QUEUE_3))
            );
    }

    @Override
    public StateDecision execute(
        final Void input,
        final Communication communication,
        final AsyncStateExecuteRequest request
    ) {
        System.out.println("PublishToLocalQueueStartingState.execute: " + input);

        // will be consumed by PublishToLocalQueueState1
        communication.publishToLocalQueue(QUEUE_1, DEDUP_ID, PAYLOAD_1);

        final List<LocalQueueMessage> localQueueResults = request.getCommandResults().getLocalQueueResults();
        Assertions.assertEquals(1, localQueueResults.size());
        Assertions.assertEquals(QUEUE_1, localQueueResults.get(0).getQueueName());
        Assertions.assertEquals(
            PAYLOAD_1_2,
            new JacksonJsonObjectEncoder().decode(localQueueResults.get(0).getPayload(), String.class)
        );

        return StateDecision.singleNextState(PublishToLocalQueueState1.class, null);
    }
}

class PublishToLocalQueueState1 implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest waitUntil(final Void input, final Communication communication) {
        System.out.println("PublishToLocalQueueState1.waitUntil: " + input);

        // will be consumed by itself
        communication.publishToLocalQueue(QUEUE_2);

        return new CommandRequest()
            .waitingType(CommandWaitingType.ALLOFCOMPLETION)
            .localQueueCommands(
                ImmutableList.of(
                    new LocalQueueCommand().queueName(QUEUE_1).count(2),
                    new LocalQueueCommand().queueName(QUEUE_2).count(2)
                )
            );
    }

    @Override
    public StateDecision execute(
        final Void input,
        final Communication communication,
        final AsyncStateExecuteRequest request
    ) {
        System.out.println("PublishToLocalQueueState1.execute: " + input);

        final List<LocalQueueMessage> localQueueResults = request.getCommandResults().getLocalQueueResults();
        Assertions.assertEquals(4, localQueueResults.size());

        Assertions.assertEquals(QUEUE_2, localQueueResults.get(0).getQueueName());
        Assertions.assertEquals(
            PAYLOAD_2,
            new JacksonJsonObjectEncoder().decode(localQueueResults.get(0).getPayload(), String.class)
        );

        Assertions.assertEquals(QUEUE_1, localQueueResults.get(1).getQueueName());
        Assertions.assertEquals(
            PAYLOAD_1,
            new JacksonJsonObjectEncoder().decode(localQueueResults.get(1).getPayload(), String.class)
        );

        Assertions.assertEquals(QUEUE_2, localQueueResults.get(2).getQueueName());
        Assertions.assertEquals(
            null,
            new JacksonJsonObjectEncoder().decode(localQueueResults.get(2).getPayload(), String.class)
        );

        Assertions.assertEquals(QUEUE_1, localQueueResults.get(3).getQueueName());
        Assertions.assertEquals(
            null,
            new JacksonJsonObjectEncoder().decode(localQueueResults.get(3).getPayload(), String.class)
        );

        return StateDecision.gracefulCompleteProcess();
    }
}
