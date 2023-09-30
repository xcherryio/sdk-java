package io.xdb.core.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.xdb.gen.models.CommandRequest;
import io.xdb.gen.models.StateDecision;
import org.junit.jupiter.api.Test;

public class AsyncStateTest {

    class StateNoWaitUntil implements AsyncState<Integer> {

        @Override
        public Class<Integer> getInputType() {
            return Integer.class;
        }

        @Override
        public StateDecision execute(final Integer input) {
            return null;
        }
    }

    class StateWithWaitUntil implements AsyncState<Void> {

        @Override
        public String getId() {
            return "testId";
        }

        @Override
        public Class<Void> getInputType() {
            return Void.class;
        }

        @Override
        public CommandRequest waitUntil(final Void input) {
            return null;
        }

        @Override
        public StateDecision execute(final Void input) {
            return null;
        }
    }

    @Test
    void shouldSkipWaitUntilTest() {
        final StateNoWaitUntil stateNoWaitUntil = new StateNoWaitUntil();
        assertTrue(AsyncState.shouldSkipWaitUntil(stateNoWaitUntil));
        assertEquals("StateNoWaitUntil", stateNoWaitUntil.getId());
        assertEquals(Integer.class, stateNoWaitUntil.getInputType());

        final StateWithWaitUntil stateWithWaitUntil = new StateWithWaitUntil();
        assertFalse(AsyncState.shouldSkipWaitUntil(stateWithWaitUntil));
        assertEquals("testId", stateWithWaitUntil.getId());
        assertEquals(Void.class, stateWithWaitUntil.getInputType());
    }
}
