package io.xdb.core.client;

import com.google.common.base.Strings;
import io.xdb.core.encoder.JacksonDatabaseStringEncoder;
import io.xdb.core.encoder.JacksonObjectEncoder;
import io.xdb.core.encoder.base.DatabaseStringEncoder;
import io.xdb.core.encoder.base.ObjectEncoder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClientOptions {

    private final String serverUrl;
    private final String workerUrl;
    /**
     * The namespace in which the operation should be performed.
     * If related SDK APIs don't specify a namespace, default to using the one defined here.
     * When related SDK APIs don't specify a namespace, and there is no namespace defined here, use the default value {@link ClientOptions#DEFAULT_NAMESPACE}
     */
    private final String namespace;
    private final ObjectEncoder objectEncoder;
    private final DatabaseStringEncoder databaseStringEncoder;
    private final ServerApiRetryConfig serverApiRetryConfig;

    private static final String DEFAULT_NAMESPACE = "default";

    public static ClientOptions getDefaultLocal() {
        return ClientOptions
            .builder()
            .serverUrl("http://localhost:8801")
            .workerUrl("http://localhost:8802")
            .namespace(DEFAULT_NAMESPACE)
            .objectEncoder(new JacksonObjectEncoder())
            .databaseStringEncoder(new JacksonDatabaseStringEncoder())
            .serverApiRetryConfig(ServerApiRetryConfig.getDefault())
            .build();
    }

    public String getNamespace() {
        return Strings.isNullOrEmpty(namespace) ? DEFAULT_NAMESPACE : namespace;
    }
}
