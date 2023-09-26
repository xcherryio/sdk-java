package io.xdb.core;

import feign.Feign;
import feign.Retryer;
import io.xdb.gen.api.ApiClient;
import io.xdb.gen.api.DefaultApi;
import io.xdb.gen.models.ProcessExecutionStartRequest;
import io.xdb.gen.models.ProcessExecutionStartResponse;

public class Client {

    private final DefaultApi defaultApi;
    private final ClientOptions clientOptions;

    public Client(final ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
        this.defaultApi = buildDefaultApi();
    }

    private DefaultApi buildDefaultApi() {
        final ApiClient apiClient = new ApiClient().setBasePath(clientOptions.getServerUrl());
        //        apiClient.setObjectMapper(clientOptions.getObjectEncoder().getObjectMapper());

        final Feign.Builder feignBuilder = apiClient
            .getFeignBuilder()
            .retryer(
                new Retryer.Default(
                    clientOptions.getInitialIntervalMills(),
                    clientOptions.getMaximumIntervalMills(),
                    clientOptions.getMaximumAttempts()
                )
            )
            .errorDecoder(new ServerErrorDecoder());
        apiClient.setFeignBuilder(feignBuilder);
        return apiClient.buildClient(DefaultApi.class);
    }

    public String startProcess() {
        final ProcessExecutionStartRequest request = new ProcessExecutionStartRequest()
            .processId("")
            .processType("")
            .workerUrl("");
        final ProcessExecutionStartResponse response = defaultApi.apiV1XdbServiceProcessExecutionStartPost(request);
        return response.getProcessExecutionId();
    }
}
