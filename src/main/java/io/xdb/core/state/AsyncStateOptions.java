package io.xdb.core.state;

import com.google.common.base.Strings;
import io.xdb.core.exception.ProcessDefinitionException;
import io.xdb.core.utils.ProcessUtil;
import lombok.Builder;

@Builder
public class AsyncStateOptions {

    // either state or id must be set
    private final AsyncState state;
    private final String id;

    public static AsyncStateOptionsBuilder builder(final AsyncState state) {
        return builder().state(state);
    }

    private static AsyncStateOptionsBuilder builder() {
        return new AsyncStateOptionsBuilder();
    }

    public String getId() {
        return Strings.isNullOrEmpty(id) ? ProcessUtil.getStateId(state) : id;
    }

    public void validate() {
        if (state == null && Strings.isNullOrEmpty(id)) {
            throw new ProcessDefinitionException("AsyncStateOptions: either state or id must be set.");
        }
    }
}
