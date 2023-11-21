package io.xcherry.core.worker;

import io.xcherry.core.encoder.JacksonDatabaseStringEncoder;
import io.xcherry.core.encoder.JacksonObjectEncoder;
import io.xcherry.core.encoder.base.DatabaseStringEncoder;
import io.xcherry.core.encoder.base.ObjectEncoder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WorkerServiceOptions {

    private final ObjectEncoder objectEncoder;
    private final DatabaseStringEncoder databaseStringEncoder;

    public static WorkerServiceOptions getDefault() {
        return WorkerServiceOptions
            .builder()
            .objectEncoder(new JacksonObjectEncoder())
            .databaseStringEncoder(new JacksonDatabaseStringEncoder())
            .build();
    }
}
