package io.xcherry.core.utils;

import io.xcherry.core.persistence.read_request.AppDatabaseReadRequest;
import io.xcherry.core.process.Process;
import io.xcherry.core.state.AsyncState;
import io.xcherry.gen.models.AsyncStateConfig;

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

    public static AsyncStateConfig getAsyncStateConfig(final AsyncState state, final Process process) {
        final AppDatabaseReadRequest appDatabaseReadRequest = state.getOptions() == null ||
            state.getOptions().getAppDatabaseReadRequest() == null
            ? process.getPersistenceSchema() == null ? null : process.getPersistenceSchema().getAppDatabaseReadRequest()
            : state.getOptions().getAppDatabaseReadRequest(process.getPersistenceSchema());

        AsyncStateConfig asyncStateConfig = new AsyncStateConfig()
            .skipWaitUntil(AsyncState.shouldSkipWaitUntil(state))
            .appDatabaseReadRequest(appDatabaseReadRequest == null ? null : appDatabaseReadRequest.toApiModel());

        if (state.getOptions() == null) {
            return asyncStateConfig;
        }

        asyncStateConfig =
            asyncStateConfig
                .waitUntilApiTimeoutSeconds(state.getOptions().getWaitUntilApiTimeoutSeconds())
                .executeApiTimeoutSeconds(state.getOptions().getExecuteApiTimeoutSeconds())
                .waitUntilApiRetryPolicy(state.getOptions().getWaitUntilApiRetryPolicy())
                .executeApiRetryPolicy(state.getOptions().getExecuteApiRetryPolicy())
                .stateFailureRecoveryOptions(state.getOptions().getStateFailureRecoveryOptions());

        return asyncStateConfig;
    }
}
