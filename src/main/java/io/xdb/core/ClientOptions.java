package io.xdb.core;

import lombok.Data;

@Data
public class ClientOptions {

    private final String serverUrl;
    private final ObjectEncoder objectEncoder;

    private long initialIntervalMills = 100;
    private long maximumIntervalMills = 1000;
    private int maximumAttempts = 10;
}
