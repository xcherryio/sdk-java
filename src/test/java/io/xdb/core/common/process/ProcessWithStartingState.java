package io.xdb.core.common.process;

import io.xdb.core.common.state.StateNoWaitUntil;
import io.xdb.core.common.state.StateWithWaitUntil;
import io.xdb.core.process.Process;
import io.xdb.core.process.ProcessOptions;
import io.xdb.core.state.StateSchema;

public class ProcessWithStartingState implements Process {

    @Override
    public ProcessOptions getOptions() {
        return ProcessOptions.builder().type("testType").build();
    }

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new StateNoWaitUntil(), new StateWithWaitUntil());
    }
}
