package io.xdb.core.process;

import io.xdb.core.state.StateSchema;

/**
 * The {@link Process} interface is used to define a process definition.
 * It represents a fundamental concept at the top level in XDB.
 */
public interface Process {
    /**
     *
     * @return the process options.
     */
    default ProcessOptions getOptions() {
        return ProcessOptions.builder(this.getClass()).build();
    }

    /**
     *
     * @return the state schema of all {@link io.xdb.core.state.AsyncState} defined for this process.
     */
    default StateSchema getStateSchema() {
        return StateSchema.builder().build();
    }
}
