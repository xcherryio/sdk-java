package io.xdb.core.process;

import io.xdb.core.state.StateSchema;
import io.xdb.core.utils.ClassUtil;

/**
 * The {@link Process} interface is used to define a process definition.
 * It represents a fundamental concept at the top level in XDB.
 */
public interface Process {
    default String getType() {
        return ClassUtil.getClassSimpleName(this);
    }

    default StateSchema getStateSchema() {
        return StateSchema.builder().build();
    }
}
