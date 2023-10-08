package io.xdb.core.state;

import com.google.common.base.Strings;
import io.xdb.core.utils.ProcessUtil;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class AsyncStateOptions {

    @NonNull
    private final Class<? extends AsyncState> stateClass;

    private final String id;

    public static AsyncStateOptionsBuilder builder(final Class<? extends AsyncState> stateClass) {
        return builder().stateClass(stateClass);
    }

    private static AsyncStateOptionsBuilder builder() {
        return new AsyncStateOptionsBuilder();
    }

    public String getId(final Class<? extends AsyncState> stateClass) {
        return Strings.isNullOrEmpty(id) ? ProcessUtil.getStateId(stateClass) : id;
    }
}
