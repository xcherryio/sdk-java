package io.xcherry.core.client;

import feign.Feign;
import feign.FeignException;
import feign.Retryer;
import io.xcherry.core.ServerErrorDecoder;
import io.xcherry.core.exception.HttpException;
import io.xcherry.core.exception.ServerRuntimeException;
import io.xcherry.core.exception.WaitTimeoutException;
import io.xcherry.gen.api.ApiClient;
import io.xcherry.gen.api.DefaultApi;
import io.xcherry.gen.models.ProcessExecutionDescribeRequest;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessExecutionRpcRequest;
import io.xcherry.gen.models.ProcessExecutionRpcResponse;
import io.xcherry.gen.models.ProcessExecutionStartRequest;
import io.xcherry.gen.models.ProcessExecutionStartResponse;
import io.xcherry.gen.models.ProcessExecutionStopRequest;
import io.xcherry.gen.models.ProcessExecutionStopType;
import io.xcherry.gen.models.ProcessExecutionWaitForCompletionRequest;
import io.xcherry.gen.models.ProcessExecutionWaitForCompletionResponse;
import io.xcherry.gen.models.ProcessStatus;
import io.xcherry.gen.models.PublishToLocalQueueRequest;

/**
 * {@link BasicClient} serves as a foundational client without a process {@link io.xcherry.core.registry}.
 * It represents the internal implementation of the {@link Client}.
 * However, it can also be utilized directly if there is a compelling reason, allowing you to invoke APIs on the server with no type validation checks, such as process type, queue names, and so forth.
 */
public class BasicClient {

    public static final int DEFAULT_WAIT_FOR_TIMEOUT = 30;

    private final ClientOptions clientOptions;
    private final DefaultApi defaultApi;

    public BasicClient(final ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
        this.defaultApi = buildDefaultApi();
    }

    /**
     * Start a new process execution.
     *
     * @param request       the request sent to the server.
     * @return              a unique identifier for the process execution.
     */
    public String startProcess(final ProcessExecutionStartRequest request) {
        final ProcessExecutionStartResponse response;
        try {
            response = defaultApi.apiV1XcherryServiceProcessExecutionStartPost(request);
        } catch (final FeignException.FeignClientException e) {
            throw HttpException.fromFeignException(clientOptions.getObjectEncoder(), e);
        }

        return response.getProcessExecutionId();
    }

    /**
     * Stop a process execution.
     *
     * @param namespace the namespace in which the operation should be performed.
     * @param processId a unique identifier used to differentiate between different executions of the same process type.
     * @param stopType  specify how the process execution should be stopped, either as TERMINATED or FAILED.
     *
     */
    public void stopProcess(final String namespace, final String processId, final ProcessExecutionStopType stopType) {
        final ProcessExecutionStopRequest request = new ProcessExecutionStopRequest()
            .namespace(namespace)
            .processId(processId)
            .stopType(stopType);

        try {
            defaultApi.apiV1XcherryServiceProcessExecutionStopPost(request);
        } catch (final FeignException.FeignClientException e) {
            throw HttpException.fromFeignException(clientOptions.getObjectEncoder(), e);
        }
    }

    /**
     * Get information about a specific process execution.
     *
     * @param namespace the namespace in which the operation should be performed.
     * @param processId a unique identifier used to differentiate between different executions of the same process type.
     * @return          information about the process execution.
     */
    public ProcessExecutionDescribeResponse describeCurrentProcessExecution(
        final String namespace,
        final String processId
    ) {
        final ProcessExecutionDescribeRequest request = new ProcessExecutionDescribeRequest()
            .namespace(namespace)
            .processId(processId);

        try {
            return defaultApi.apiV1XcherryServiceProcessExecutionDescribePost(request);
        } catch (final FeignException.FeignClientException e) {
            throw HttpException.fromFeignException(clientOptions.getObjectEncoder(), e);
        }
    }

    /**
     * Publish message(s) to the local queue for consumption by the process execution.
     *
     * @param request   the request sent to the server.
     */
    public void publishToLocalQueue(final PublishToLocalQueueRequest request) {
        try {
            defaultApi.apiV1XcherryServiceProcessExecutionPublishToLocalQueuePost(request);
        } catch (final FeignException.FeignClientException e) {
            throw HttpException.fromFeignException(clientOptions.getObjectEncoder(), e);
        }
    }

    /**
     * Invoke an RPC method through the rpc stub.
     *
     * @param request       the request to invoke the RPC method.
     * @param returnType    the return type of the RPC method.
     * @return  the output of the RPC execution.
     * @param <T>   the output type.
     */
    public <T> T invokeRPC(final ProcessExecutionRpcRequest request, final Class<T> returnType) {
        final ProcessExecutionRpcResponse response;
        try {
            response = defaultApi.apiV1XcherryServiceProcessExecutionRpcPost(request);
        } catch (final FeignException.FeignClientException e) {
            throw HttpException.fromFeignException(clientOptions.getObjectEncoder(), e);
        }

        return clientOptions.getObjectEncoder().decodeFromEncodedObject(response.getOutput(), returnType);
    }

    /**
     * Wait for a process execution to complete within the specified timeout seconds.
     *  1. If the process execution is still running after the timeout seconds, {@link WaitTimeoutException} will be thrown.
     *  2. If the process execution has stopped by system (e.g., due to re-balancing in the server), {@link ServerRuntimeException} will be thrown.
     *  3. In other cases, the current status of the process execution will be returned.
     *
     * @param namespace         the namespace in which the operation should be performed.
     * @param processId         a unique identifier used to differentiate between different executions of the same process type.
     * @param timeoutInSeconds  a value less than or equal to 30.
     * @return the process status
     */
    public ProcessStatus waitForProcessCompletion(
        final String namespace,
        final String processId,
        final int timeoutInSeconds
    ) {
        if (timeoutInSeconds <= 0 || timeoutInSeconds > DEFAULT_WAIT_FOR_TIMEOUT) {
            throw new RuntimeException(
                "timeoutInSeconds must be a value greater than 0 and less than or equal to " + DEFAULT_WAIT_FOR_TIMEOUT
            );
        }

        final ProcessExecutionWaitForCompletionRequest request = new ProcessExecutionWaitForCompletionRequest()
            .namespace(namespace)
            .processId(processId)
            .timeoutSeconds(timeoutInSeconds);

        final ProcessExecutionWaitForCompletionResponse waitForCompletionResponse;
        try {
            waitForCompletionResponse =
                defaultApi.apiV1XcherryServiceProcessExecutionWaitForProcessCompletionPost(request);
        } catch (final FeignException.FeignClientException e) {
            throw HttpException.fromFeignException(clientOptions.getObjectEncoder(), e);
        }

        if (Boolean.TRUE.equals(waitForCompletionResponse.getTimeout())) {
            throw new WaitTimeoutException("waitForProcessCompletion times out in " + timeoutInSeconds + " seconds");
        }

        if (Boolean.TRUE.equals(waitForCompletionResponse.getStopBySystem())) {
            throw new ServerRuntimeException("waitForProcessCompletion stops by system");
        }

        return waitForCompletionResponse.getStatus();
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
