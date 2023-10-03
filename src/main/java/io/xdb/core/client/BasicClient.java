package io.xdb.core.client;

import feign.Feign;
import feign.FeignException;
import feign.Retryer;
import io.xdb.core.ServerErrorDecoder;
import io.xdb.core.exception.XDBHttpException;
import io.xdb.core.process.BasicClientProcessOptions;
import io.xdb.core.process.ProcessOptions;
import io.xdb.gen.api.ApiClient;
import io.xdb.gen.api.DefaultApi;
import io.xdb.gen.models.ProcessExecutionDescribeRequest;
import io.xdb.gen.models.ProcessExecutionDescribeResponse;
import io.xdb.gen.models.ProcessExecutionStartRequest;
import io.xdb.gen.models.ProcessExecutionStartResponse;
import io.xdb.gen.models.ProcessStartConfig;

/**
 * {@link BasicClient} serves as a foundational client without a process {@link io.xdb.core.registry}.
 * It represents the internal implementation of the {@link Client}.
 * However, it can also be utilized directly if there is a compelling reason, allowing you to invoke APIs on the xdb server with no type validation checks, such as process type, queue names, and so forth.
 */
public class BasicClient {

    private final ClientOptions clientOptions;
    private final DefaultApi defaultApi;

    public BasicClient(final ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
        this.defaultApi = buildDefaultApi();
    }

    public String startProcess(
        final String processType,
        final String processId,
        final String startStateId,
        final Object input,
        final BasicClientProcessOptions processOptions
    ) {
        final ProcessExecutionStartRequest request = new ProcessExecutionStartRequest()
            .processType(processType)
            .processId(processId)
            .workerUrl(clientOptions.getWorkerUrl())
            .startStateId(startStateId)
            .startStateInput(clientOptions.getObjectEncoder().encode(input));

        if (processOptions.getProcessOptionsOptional().isPresent()) {
            final ProcessOptions options = processOptions.getProcessOptionsOptional().get();
            request.processStartConfig(
                new ProcessStartConfig()
                    .idReusePolicy(options.getProcessIdReusePolicy())
                    .timeoutSeconds(options.getTimeoutSeconds())
            );
        }

        if (processOptions.getStartStateConfig().isPresent()) {
            request.startStateConfig(processOptions.getStartStateConfig().get());
        }

        final ProcessExecutionStartResponse response;
        try {
            response = defaultApi.apiV1XdbServiceProcessExecutionStartPost(request);
        } catch (final FeignException.FeignClientException e) {
            throw XDBHttpException.fromFeignException(clientOptions.getObjectEncoder(), e);
        }

        return response.getProcessExecutionId();
    }

    public ProcessExecutionDescribeResponse describeCurrentProcessExecution(final String processId) {
        final ProcessExecutionDescribeRequest request = new ProcessExecutionDescribeRequest().processId(processId);

        try {
            return defaultApi.apiV1XdbServiceProcessExecutionDescribePost(request);
        } catch (final FeignException.FeignClientException e) {
            throw XDBHttpException.fromFeignException(clientOptions.getObjectEncoder(), e);
        }
    }

    private DefaultApi buildDefaultApi() {
        final ApiClient apiClient = new ApiClient().setBasePath(clientOptions.getServerUrl());
        apiClient.setObjectMapper(clientOptions.getObjectEncoder().getObjectMapper());

        final Feign.Builder feignBuilder = apiClient.getFeignBuilder();
        final ServerApiRetryConfig apiRetryConfig = clientOptions.getServerApiRetryConfig();
        feignBuilder.retryer(
            new Retryer.Default(
                apiRetryConfig.getInitialIntervalMills(),
                apiRetryConfig.getMaximumIntervalMills(),
                apiRetryConfig.getMaximumAttempts()
            )
        );
        feignBuilder.errorDecoder(new ServerErrorDecoder());
        apiClient.setFeignBuilder(feignBuilder);

        return apiClient.buildClient(DefaultApi.class);
    }
}
