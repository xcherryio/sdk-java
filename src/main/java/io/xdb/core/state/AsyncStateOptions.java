package io.xdb.core.state;

import com.google.common.base.Strings;
import io.xdb.core.utils.ProcessUtil;
import lombok.Builder;

@Builder
public class AsyncStateOptions {

    private final String id;

    public String getId(final Class<? extends AsyncState> stateClass) {
        return Strings.isNullOrEmpty(id) ? ProcessUtil.getClassSimpleName(stateClass) : id;
    }
}
