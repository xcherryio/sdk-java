package integ.command_request;

import static integ.command_request.TestCommandRequest.QUEUE_1;

import io.xdb.core.command.CommandRequest;
import io.xdb.core.command.CommandResults;
import io.xdb.core.command.LocalQueueCommand;
import io.xdb.core.command.TimerCommand;
import io.xdb.core.communication.Communication;
import io.xdb.core.context.Context;
import io.xdb.core.persistence.Persistence;
import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateDecision;
import io.xdb.core.state.StateSchema;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class CommandRequestAllCompleteProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new CommandRequestAllCompleteProcessStartingState());
    }
}

class CommandRequestAllCompleteProcessStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Void input, final Communication communication) {
        System.out.println("CommandRequestAllCompleteProcessStartingState.waitUntil: " + input);

        return CommandRequest.allOfCommandsComplete(
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
        System.out.println("CommandRequestAllCompleteProcessStartingState.execute: " + input);

        return StateDecision.gracefulCompleteProcess();
    }
}
