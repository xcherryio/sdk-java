package integ.command_request;

import static integ.command_request.TestCommandRequest.QUEUE_1;

import io.xdb.core.command.CommandRequest;
import io.xdb.core.command.LocalQueueCommand;
import io.xdb.core.command.TimerCommand;
import io.xdb.core.communication.Communication;
import io.xdb.core.persistence.Persistence;
import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateDecision;
import io.xdb.core.state.StateSchema;
import io.xdb.gen.models.CommandResults;
import io.xdb.gen.models.Context;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class CommandRequestAnyCompleteProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new CommandRequestAnyCompleteProcessStartingState());
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
