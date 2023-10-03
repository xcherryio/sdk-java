package io.xdb.core.common.process;

import io.xdb.core.common.state.StateWithWaitUntil;
import io.xdb.core.process.Process;
import io.xdb.core.state.StateSchema;

public class ProcessNoStartingState implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.noStartingState(new StateWithWaitUntil());
    }
}
