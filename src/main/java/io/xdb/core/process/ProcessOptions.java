package io.xdb.core.process;

import com.google.common.base.Strings;
import io.xdb.core.exception.ProcessDefinitionException;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.ProcessStartConfig;
import lombok.Builder;

@Builder
public class ProcessOptions {

    // either process or type must be set
    private final Process process;
    // If not set, use the default value.
    private final String namespace;
    // either process or type must be set
    private final String type;
    private final ProcessStartConfig processStartConfig;

    public static final String DEFAULT_NAMESPACE = "default";

    public static ProcessOptionsBuilder builder(final Process process) {
        return builder().process(process);
    }

    private static ProcessOptionsBuilder builder() {
        return new ProcessOptionsBuilder();
    }

    public String getNamespace() {
        return Strings.isNullOrEmpty(namespace) ? DEFAULT_NAMESPACE : namespace;
    }

    public String getType() {
        return Strings.isNullOrEmpty(type) ? ProcessUtil.getProcessType(process) : type;
    }

    public ProcessStartConfig getProcessStartConfig() {
        return processStartConfig;
    }

    public void validate() {
        if (process == null && Strings.isNullOrEmpty(type)) {
            throw new ProcessDefinitionException("ProcessOptions: either process or type must be set.");
        }
    }
}
