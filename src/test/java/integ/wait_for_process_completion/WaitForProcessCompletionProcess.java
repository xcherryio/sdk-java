package integ.wait_for_process_completion;

import static integ.wait_for_process_completion.TestWaitForProcessCompletion.QUEUE_1;

import io.xcherry.core.command.CommandRequest;
import io.xcherry.core.command.CommandResults;
import io.xcherry.core.command.LocalQueueCommand;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.persistence.Persistence;
import io.xcherry.core.process.Process;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.StateDecision;
import io.xcherry.core.state.StateSchema;
import org.springframework.stereotype.Component;

@Component
public class WaitForProcessCompletionProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new WaitForProcessCompletionStartingState());
    }
}

class WaitForProcessCompletionStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Void input, final Communication communication) {
        System.out.println("WaitForProcessCompletionStartingState.waitUntil");

        return CommandRequest.allOfCommandsComplete(LocalQueueCommand.create(QUEUE_1));
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Void input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("WaitForProcessCompletionStartingState.execute");

        return StateDecision.forceCompleteProcess();
    }
}
