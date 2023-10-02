package io.xdb.core.process;

import io.xdb.gen.models.ProcessIdReusePolicy;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessOptions {

    private final String type;
    private final ProcessIdReusePolicy processIdReusePolicy;
    private final Integer timeoutSeconds;
}
