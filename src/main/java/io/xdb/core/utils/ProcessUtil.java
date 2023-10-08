package io.xdb.core.utils;

import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.gen.models.AsyncStateConfig;

public class ProcessUtil {

    public static String getProcessType(final Class<? extends Process> processClass) {
        return processClass.getSimpleName();
    }

    public static String getStateId(final Class<? extends AsyncState> stateClass) {
        return stateClass.getSimpleName();
    }

    public static AsyncStateConfig getAsyncStateConfig(final AsyncState state) {
        return new AsyncStateConfig().skipWaitUntil(AsyncState.shouldSkipWaitUntil(state));
    }
}
