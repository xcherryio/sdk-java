package io.xdb.core.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.xdb.core.common.process.ProcessNoStartingState;
import io.xdb.core.common.process.ProcessWithStartingState;
import io.xdb.core.utils.ProcessUtil;
import org.junit.jupiter.api.Test;

public class RegistryTest {

    @Test
    void addProcessesTest() {
        final Registry registry = new Registry();
        final ProcessWithStartingState processWithStartingState = new ProcessWithStartingState();
        final ProcessNoStartingState processNoStartingState = new ProcessNoStartingState();

        registry.addProcesses(processWithStartingState, processNoStartingState);

        assertEquals(
            processWithStartingState,
            registry.getProcess(ProcessUtil.getProcessType(processWithStartingState))
        );
        assertEquals(processNoStartingState, registry.getProcess(ProcessUtil.getProcessType(processNoStartingState)));

        assertTrue(registry.getProcessStartingState(ProcessUtil.getProcessType(processWithStartingState)).isPresent());
        assertFalse(registry.getProcessStartingState(ProcessUtil.getProcessType(processNoStartingState)).isPresent());
    }
}
