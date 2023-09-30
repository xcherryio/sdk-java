package io.xdb.core.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.xdb.core.common.state.StateNoWaitUntil;
import io.xdb.core.common.state.StateWithWaitUntil;
import org.junit.jupiter.api.Test;

public class AsyncStateTest {

    @Test
    void shouldSkipWaitUntilTest() {
        final StateNoWaitUntil stateNoWaitUntil = new StateNoWaitUntil();
        assertTrue(AsyncState.shouldSkipWaitUntil(stateNoWaitUntil));
        assertEquals("StateNoWaitUntil", stateNoWaitUntil.getId());
        assertEquals(Void.class, stateNoWaitUntil.getInputType());

        final StateWithWaitUntil stateWithWaitUntil = new StateWithWaitUntil();
        assertFalse(AsyncState.shouldSkipWaitUntil(stateWithWaitUntil));
        assertEquals("testStateId", stateWithWaitUntil.getId());
        assertEquals(Integer.class, stateWithWaitUntil.getInputType());
    }
}
