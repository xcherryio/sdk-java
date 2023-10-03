package io.xdb.core.common.state;

import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateOptions;
import io.xdb.gen.models.CommandRequest;
import io.xdb.gen.models.StateDecision;

public class StateWithWaitUntil implements AsyncState<Integer> {

    @Override
    public StateOptions getOptions() {
        return StateOptions.builder().id("testStateId").build();
    }

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(final Integer input) {
        return null;
    }

    @Override
    public StateDecision execute(final Integer input) {
        return null;
    }
}
