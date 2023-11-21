package io.xcherry.core.process;

import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.StateSchema;

/**
 * The {@link Process} interface is used to define a process definition.
 * It represents a fundamental concept at the top level.
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
     * @return the state schema of all {@link AsyncState} defined for this process.
     */
    default StateSchema getStateSchema() {
        return StateSchema.noStartingState();
    }

    /**
     * The persistence schema defines the global attributes to read/write in the process.
     * <p>
     * Global attribute: is the value of a specified database table row column.
     *
     * @return the persistence schema defined for this process.
     */
    default PersistenceSchema getPersistenceSchema() {
        return PersistenceSchema.EMPTY();
    }
}
