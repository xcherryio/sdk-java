package io.xdb.core.process;

import io.xdb.gen.models.ProcessIdReusePolicy;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessOptions {

    protected final ProcessIdReusePolicy processIdReusePolicy;
    protected final Integer timeoutSeconds;
}
