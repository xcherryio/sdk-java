package io.xdb.core.utils;

import com.google.common.base.Strings;
import io.xdb.core.process.Process;
import io.xdb.core.process.ProcessOptions;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateOptions;

public class ProcessUtil {

    public static String getProcessType(final Class<? extends Process> processClass) {
        return processClass.getSimpleName();
    }

    public static String getProcessType(final Process process) {
        final ProcessOptions options = process.getOptions();
        if (Strings.isNullOrEmpty(options.getType())) {
            return getProcessType(process.getClass());
        }
        return options.getType();
    }

    public static String getStateId(final Class<? extends AsyncState> stateClass) {
        return stateClass.getSimpleName();
    }

    public static String getStateId(final AsyncState state) {
        final StateOptions options = state.getOptions();
        if (Strings.isNullOrEmpty(options.getId())) {
            return getStateId(state.getClass());
        }
        return options.getId();
    }
}
