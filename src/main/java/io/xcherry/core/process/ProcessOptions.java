package io.xcherry.core.process;

import com.google.common.base.Strings;
import io.xcherry.core.exception.ProcessDefinitionException;
import io.xcherry.core.utils.ProcessUtil;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessOptions {

    /**
     * Either processClass or type must be set.
     */
    private final Class<? extends Process> processClass;
    /**
     * Either processClass or type must be set.
     */
    private final String type;

    public static ProcessOptionsBuilder builder(final Class<? extends Process> processClass) {
        return builder().processClass(processClass);
    }

    private static ProcessOptionsBuilder builder() {
        return new ProcessOptionsBuilder();
    }

    public String getType() {
        return Strings.isNullOrEmpty(type) ? ProcessUtil.getClassSimpleName(processClass) : type;
    }

    public void validate() {
        if (processClass == null && Strings.isNullOrEmpty(type)) {
            throw new ProcessDefinitionException("ProcessOptions: either processClass or type must be set.");
        }
    }
}
