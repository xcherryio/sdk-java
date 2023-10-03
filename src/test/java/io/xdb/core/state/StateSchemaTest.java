package io.xdb.core.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.xdb.core.common.state.StateNoWaitUntil;
import io.xdb.core.common.state.StateWithWaitUntil;
import org.junit.jupiter.api.Test;

public class StateSchemaTest {

    @Test
    void withStartingStateTest() {
        final StateNoWaitUntil startingState = new StateNoWaitUntil();
        final StateSchema stateSchema = StateSchema.withStartingState(startingState, new StateWithWaitUntil());

        assertEquals(startingState, stateSchema.getStartingState());
        assertEquals(2, stateSchema.getAllStates().size());
    }

    @Test
    void noStartingStateTest() {
        final StateSchema stateSchema = StateSchema.noStartingState(new StateNoWaitUntil(), new StateWithWaitUntil());

        assertEquals(null, stateSchema.getStartingState());
        assertEquals(2, stateSchema.getAllStates().size());
    }
}
