package io.xdb.core.client;

import io.xdb.core.encoder.JacksonJsonObjectEncoder;
import io.xdb.core.encoder.ObjectEncoder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClientOptions {

    private final String serverUrl;
    private final String workerUrl;
    private final ObjectEncoder objectEncoder;
    private final ServerApiRetryConfig serverApiRetryConfig;

    public static ClientOptions getDefaultLocal() {
        return ClientOptions
            .builder()
            .serverUrl("http://localhost:8801")
            .workerUrl("http://localhost:8802")
            .objectEncoder(new JacksonJsonObjectEncoder())
            .serverApiRetryConfig(ServerApiRetryConfig.getDefault())
            .build();
    }
}
