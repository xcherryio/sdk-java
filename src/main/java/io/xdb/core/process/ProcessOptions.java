package io.xdb.core.process;

import com.google.common.base.Strings;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.ProcessStartConfig;
import lombok.Builder;

@Builder
public class ProcessOptions {

    // If not set, use the default value.
    private final String namespace;
    private final String type;
    private final ProcessStartConfig processStartConfig;

    public String getNamespace() {
        return Strings.isNullOrEmpty(namespace) ? "default" : namespace;
    }

    public String getType(final Class<? extends Process> processClass) {
        return Strings.isNullOrEmpty(type) ? ProcessUtil.getClassSimpleName(processClass) : type;
    }

    public ProcessStartConfig getProcessStartConfig() {
        return processStartConfig;
    }
}
