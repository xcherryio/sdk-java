package io.xdb.core.process;

import com.google.common.base.Strings;
import io.xdb.core.exception.ProcessDefinitionException;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.ProcessStartConfig;
import lombok.Builder;

@Builder
public class ProcessOptions {

    // either processClass or type must be set
    private final Class<? extends Process> processClass;
    // If not set, use the default value.
    private final String namespace;
    // either processClass or type must be set
    private final String type;
    private final ProcessStartConfig processStartConfig;

    public static final String DEFAULT_NAMESPACE = "default";

    public static ProcessOptionsBuilder builder(final Class<? extends Process> processClass) {
        return builder().processClass(processClass);
    }

    private static ProcessOptionsBuilder builder() {
        return new ProcessOptionsBuilder();
    }

    public String getNamespace() {
        return Strings.isNullOrEmpty(namespace) ? DEFAULT_NAMESPACE : namespace;
    }

    public String getType() {
        return Strings.isNullOrEmpty(type) ? ProcessUtil.getClassSimpleName(processClass) : type;
    }

    public ProcessStartConfig getProcessStartConfig() {
        return processStartConfig;
    }

    public void validate() {
        if (processClass == null && Strings.isNullOrEmpty(type)) {
            throw new ProcessDefinitionException("ProcessOptions: either processClass or type must be set.");
        }
    }
}
