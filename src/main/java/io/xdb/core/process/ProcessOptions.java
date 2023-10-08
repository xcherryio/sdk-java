package io.xdb.core.process;

import com.google.common.base.Strings;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.ProcessStartConfig;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class ProcessOptions {

    @NonNull
    private final Class<? extends Process> processClass;

    // If not set, use the default value.
    private final String namespace;
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
        return Strings.isNullOrEmpty(type) ? ProcessUtil.getProcessType(processClass) : type;
    }

    public ProcessStartConfig getProcessStartConfig() {
        return processStartConfig;
    }
}
