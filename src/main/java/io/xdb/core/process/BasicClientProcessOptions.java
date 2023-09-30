package io.xdb.core.process;

import io.xdb.gen.models.AsyncStateConfig;
import java.util.Optional;
import lombok.Getter;

@Getter
public class BasicClientProcessOptions {

    private final Optional<ProcessOptions> processOptionsOptional;
    private final AsyncStateConfig startStateConfig;

    public BasicClientProcessOptions(final ProcessOptions processOptions, final AsyncStateConfig startStateConfig) {
        if (processOptions == null) {
            this.processOptionsOptional = Optional.empty();
        } else {
            this.processOptionsOptional = Optional.of(processOptions);
        }

        this.startStateConfig = startStateConfig;
    }
}
