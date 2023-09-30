package io.xdb.core.client;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ServerApiRetryConfig {

    private long initialIntervalMills;
    private long maximumIntervalMills;
    private int maximumAttempts;

    public static ServerApiRetryConfig getDefault() {
        return ServerApiRetryConfig
            .builder()
            .initialIntervalMills(100)
            .maximumIntervalMills(1000)
            .maximumAttempts(10)
            .build();
    }
}
