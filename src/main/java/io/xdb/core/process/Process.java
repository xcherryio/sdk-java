package io.xdb.core.process;

import io.xdb.core.state.StateSchema;

/**
 * The {@link Process} interface is used to define a process definition.
 * It represents a fundamental concept at the top level in XDB.
 */
public interface Process {
    default ProcessOptions getOptions() {
        return ProcessOptions.builder(this).build();
    }

    default StateSchema getStateSchema() {
        return StateSchema.builder().build();
    }
}
