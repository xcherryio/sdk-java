package integ.id_reuse_policy;

import io.xdb.core.command.CommandResults;
import io.xdb.core.communication.Communication;
import io.xdb.core.context.Context;
import io.xdb.core.persistence.Persistence;
import io.xdb.core.process.Process;
import io.xdb.core.process.ProcessOptions;
import io.xdb.core.process.ProcessStartConfig;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateDecision;
import io.xdb.core.state.StateSchema;
import io.xdb.gen.models.ProcessIdReusePolicy;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class AllowIfPreviousExitAbnormallyProcess implements Process {

    @Override
    public ProcessOptions getOptions() {
        return ProcessOptions
            .builder(AllowIfPreviousExitAbnormallyProcess.class)
            .processStartConfig(
                ProcessStartConfig
                    .builder()
                    .processIdReusePolicy(ProcessIdReusePolicy.ALLOW_IF_PREVIOUS_EXIT_ABNORMALLY)
                    .build()
            )
            .build();
    }

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new AllowIfPreviousExitAbnormallyStartingState());
    }
}

class AllowIfPreviousExitAbnormallyStartingState implements AsyncState<Void> {

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
