package io.xdb.core.worker;

import io.xdb.core.encoder.JacksonDatabaseStringEncoder;
import io.xdb.core.encoder.JacksonObjectEncoder;
import io.xdb.core.encoder.base.DatabaseStringEncoder;
import io.xdb.core.encoder.base.ObjectEncoder;
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
