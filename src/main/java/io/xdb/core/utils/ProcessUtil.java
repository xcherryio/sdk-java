package io.xdb.core.utils;

import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.gen.models.AsyncStateConfig;

public class ProcessUtil {

    /**
     * The default method to get type/id from an objectClass.
     * Only use it when there is no definition of the {@link Process} / {@link AsyncState}
     *
     * @param objectClass
     * @return
     */
    public static String getClassSimpleName(final Class<?> objectClass) {
        return objectClass.getSimpleName();
    }

    public static String getProcessType(final Process process) {
        if (process.getOptions() == null) {
            return getClassSimpleName(process.getClass());
        }
        return process.getOptions().getType();
    }

    public static String getStateId(final AsyncState state) {
        if (state.getOptions() == null) {
            return getClassSimpleName(state.getClass());
        }
        return state.getOptions().getId();
    }

    public static AsyncStateConfig getAsyncStateConfig(final AsyncState state) {
        return new AsyncStateConfig()
            .skipWaitUntil(AsyncState.shouldSkipWaitUntil(state))
            .waitUntilApiTimeoutSeconds(state.getOptions().getWaitUntilApiTimeoutSeconds())
            .executeApiTimeoutSeconds(state.getOptions().getExecuteApiTimeoutSeconds())
            .waitUntilApiRetryPolicy(state.getOptions().getWaitUntilApiRetryPolicy())
            .executeApiRetryPolicy(state.getOptions().getExecuteApiRetryPolicy())
            .stateFailureRecoveryOptions(state.getOptions().getStateFailureRecoveryOptions())
            .loadGlobalAttributesRequest(state.getOptions().getLoadGlobalAttributesRequest());
    }
}
