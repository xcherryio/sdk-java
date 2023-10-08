package io.xdb.core.process;

import io.xdb.core.state.StateSchema;
import lombok.NonNull;

/**
 * The {@link Process} interface is used to define a process definition.
 * It represents a fundamental concept at the top level in XDB.
 */
public interface Process {
    @NonNull
    default ProcessOptions getOptions() {
        return ProcessOptions.builder(this.getClass()).build();
    }

    @NonNull
    default StateSchema getStateSchema() {
        return StateSchema.builder().build();
    }
}
