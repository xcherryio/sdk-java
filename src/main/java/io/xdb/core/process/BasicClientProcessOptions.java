package io.xdb.core.process;

import io.xdb.gen.models.AsyncStateConfig;
import java.util.Optional;
import lombok.Getter;

@Getter
public class BasicClientProcessOptions {

    private final Optional<ProcessOptions> processOptionsOptional;
    private final Optional<AsyncStateConfig> startStateConfig;

    public BasicClientProcessOptions(final ProcessOptions processOptions, final AsyncStateConfig startStateConfig) {
        this.processOptionsOptional = Optional.ofNullable(processOptions);
        this.startStateConfig = Optional.of(startStateConfig);
    }
}
