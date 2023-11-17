package integ.id_reuse_policy;

import io.xdb.core.command.CommandResults;
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
public class IdReusePolicyAutoCompleteProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new IdReusePolicyAutoCompleteStartingState());
    }
}

class IdReusePolicyAutoCompleteStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Void input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        try {
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        return StateDecision.forceCompleteProcess();
    }
}
