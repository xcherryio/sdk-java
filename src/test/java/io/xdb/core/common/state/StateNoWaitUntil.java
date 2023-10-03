package io.xdb.core.common.state;

import io.xdb.core.state.AsyncState;
import io.xdb.gen.models.StateDecision;

public class StateNoWaitUntil implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public StateDecision execute(final Void input) {
        return null;
    }
}
