package io.xdb.core.worker;

import io.xdb.core.encoder.JacksonJsonObjectEncoder;
import io.xdb.core.encoder.ObjectEncoder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WorkerServiceOptions {

    private final ObjectEncoder objectEncoder;

    public static WorkerServiceOptions getDefault() {
        return WorkerServiceOptions.builder().objectEncoder(new JacksonJsonObjectEncoder()).build();
    }
}
