package integ.command_request;

import static integ.command_request.TestCommandRequest.QUEUE_1;

import com.google.common.collect.ImmutableList;
import io.xdb.core.command.CommandRequest;
import io.xdb.core.command.CommandResults;
import io.xdb.core.command.LocalQueueCommand;
import io.xdb.core.command.TimerCommand;
import io.xdb.core.communication.Communication;
import io.xdb.core.communication.CommunicationSchema;
import io.xdb.core.communication.LocalQueueDef;
import io.xdb.core.context.Context;
import io.xdb.core.persistence.Persistence;
import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateDecision;
import io.xdb.core.state.StateSchema;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class CommandRequestAnyCompleteProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new CommandRequestAnyCompleteProcessStartingState());
    }

    @Override
    public CommunicationSchema getCommunicationSchema() {
        return CommunicationSchema
            .builder()
            .localQueueDefs(ImmutableList.of(LocalQueueDef.create(QUEUE_1, Void.class)))
            .build();
    }
}

class CommandRequestAnyCompleteProcessStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Void input, final Communication communication) {
        System.out.println("CommandRequestAnyCompleteProcessStartingState.waitUntil: " + input);

        return CommandRequest.anyOfCommandsComplete(
            TimerCommand.byDuration(Duration.ofSeconds(10)),
            LocalQueueCommand.create(QUEUE_1)
        );
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Void input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("CommandRequestAnyCompleteProcessStartingState.execute: " + input);

        return StateDecision.gracefulCompleteProcess();
    }
}
