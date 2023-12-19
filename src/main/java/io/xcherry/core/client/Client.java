package io.xcherry.core.client;

import io.xcherry.core.exception.RpcException;
import io.xcherry.core.process.Process;
import io.xcherry.core.process.ProcessStartConfig;
import io.xcherry.core.registry.Registry;
import io.xcherry.core.rpc.RpcDefinition;
import io.xcherry.core.rpc.RpcInterceptor;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.utils.ProcessUtil;
import io.xcherry.gen.models.LocalQueueMessage;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessExecutionStartRequest;
import io.xcherry.gen.models.ProcessExecutionStopType;
import io.xcherry.gen.models.PublishToLocalQueueRequest;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class Client {

    private final Registry registry;
    private final ClientOptions clientOptions;

    private final BasicClient basicClient;

    public Client(final Registry registry, final ClientOptions clientOptions) {
        this.registry = registry;
        this.clientOptions = clientOptions;
        this.basicClient = new BasicClient(clientOptions);
    }

    /**
     * Start a new process execution.
     *
     * @param process       the target process to start.
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param input         the input data to be provided for the execution.
     * @return              a unique identifier for the process execution.
     */
    public String startProcess(final Process process, final String processId, final Object input) {
        return startProcess(process, processId, input, null);
    }

    /**
     * Start a new process execution.
     *
     * @param process               the target process to start.
     * @param processId             a unique identifier used to differentiate between different executions of the same process type.
     * @param input                 the input data to be provided for the execution.
     * @param processStartConfig    the config to apply when starting the process.
     * @return              a unique identifier for the process execution.
     */
    public String startProcess(
        final Process process,
        final String processId,
        final Object input,
        final ProcessStartConfig processStartConfig
    ) {
        final String processType = ProcessUtil.getProcessType(process);
        return startProcessInternal(processType, processId, input, processStartConfig);
    }

    /**
     * Caution: if you intend to override certain process options, utilize the {@link Client#startProcess(Process, String, Object)} method
     * Start a new process execution.
     *
     * @param processClass  the class of the target process to start.
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param input         the input data to be provided for the execution.
     * @return              a unique identifier for the process execution.
     */
    public String startProcess(
        final Class<? extends Process> processClass,
        final String processId,
        final Object input
    ) {
        return startProcess(processClass, processId, input, null);
    }

    /**
     * Caution: if you intend to override certain process options, utilize the {@link Client#startProcess(Process, String, Object)} method
     * Start a new process execution.
     *
     * @param processClass  the class of the target process to start.
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param input         the input data to be provided for the execution.
     * @param processStartConfig    the config to apply when starting the process.
     * @return              a unique identifier for the process execution.
     */
    public String startProcess(
        final Class<? extends Process> processClass,
        final String processId,
        final Object input,
        final ProcessStartConfig processStartConfig
    ) {
        final String processType = ProcessUtil.getClassSimpleName(processClass);
        return startProcessInternal(processType, processId, input, processStartConfig);
    }

    /**
     * Stop a process execution as TERMINATED.
     *
     * @param processId a unique identifier used to differentiate between different executions of the same process type.
     */
    public void stopProcess(final String processId) {
        stopProcess(processId, ProcessExecutionStopType.TERMINATE);
    }

    /**
     * Stop a process execution.
     *
     * @param processId a unique identifier used to differentiate between different executions of the same process type.
     * @param stopType  specify how the process execution should be stopped, either as TERMINATED or FAILED.
     */
    public void stopProcess(final String processId, final ProcessExecutionStopType stopType) {
        basicClient.stopProcess(clientOptions.getNamespace(), processId, stopType);
    }

    /**
     * Publish message(s) to the local queue for consumption by the process execution.
     *
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param queueNames    queue names for categorizing message types.
     */
    public void publishToLocalQueue(final String processId, final String... queueNames) {
        final LocalQueueMessage[] messages = Arrays
            .stream(queueNames)
            .map(queueName -> new LocalQueueMessage().queueName(queueName))
            .toArray(LocalQueueMessage[]::new);

        publishToLocalQueue(processId, messages);
    }

    /**
     * Publish a message to the local queue for consumption by the process execution.
     *
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param queueName     queue name for categorizing message types.
     * @param payload       payload of the message.
     */
    public void publishToLocalQueue(final String processId, final String queueName, final Object payload) {
        publishToLocalQueue(processId, queueName, "", payload);
    }

    /**
     * Publish a message to the local queue for consumption by the process execution.
     *
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param queueName     queue name for categorizing message types.
     * @param dedupId       UUID to uniquely distinguish different messages. If not specified, the server will generate a UUID instead.
     * @param payload       payload of the message.
     */
    public void publishToLocalQueue(
        final String processId,
        final String queueName,
        final String dedupId,
        final Object payload
    ) {
        final LocalQueueMessage message = new LocalQueueMessage()
            .queueName(queueName)
            .dedupId(dedupId)
            .payload(clientOptions.getObjectEncoder().encodeToEncodedObject(payload));

        publishToLocalQueue(processId, message);
    }

    /**
     * Get information about a specific process execution.
     *
     * @param processId a unique identifier used to differentiate between different executions of the same process type.
     * @return          information about the process execution.
     */
    public ProcessExecutionDescribeResponse describeCurrentProcessExecution(final String processId) {
        return basicClient.describeCurrentProcessExecution(clientOptions.getNamespace(), processId);
    }

    /**
     * Create a new stub for invoking RPC methods.
     *
     * @param processClass      class of the target process that the RPC methods belong to.
     * @param processId         a unique identifier used to differentiate between different executions of the same process type.
     * @return  a new rpc stub.
     * @param <T>               the target process type.
     */
    public <T extends Process> T newStubForRPC(final Class<T> processClass, final String processId) {
        final Class<? extends T> dynamicType = new ByteBuddy()
            .subclass(processClass)
            .method(ElementMatchers.any())
            .intercept(
                MethodDelegation.to(
                    new RpcInterceptor(
                        basicClient,
                        clientOptions.getNamespace(),
                        processId,
                        clientOptions.getObjectEncoder()
                    )
                )
            )
            .make()
            .load(getClass().getClassLoader())
            .getLoaded();

        try {
            return dynamicType.newInstance();
        } catch (final Exception e) {
            throw new RpcException(
                String.format(
                    "Failed to create new RPC stub with process class %s and process id %s",
                    processClass.getSimpleName(),
                    processId
                ),
                e
            );
        }
    }

    /**
     * Invoke an RPC method through the rpc stub.
     *
     * @param rpcMethod     the RPC method from stub created by {@link #newStubForRPC(Class, String)}}
     * @param input         the input of the RPC method.
     * @return  the output of the RPC execution.
     * @param <I>           the input type.
     * @param <O>           the output type.
     */
    public <I, O> O invokeRPC(final RpcDefinition.RpcMethod<I, O> rpcMethod, final I input) {
        return rpcMethod.execute(null, input, null, null);
    }

    /**
     * Invoke an RPC method through the rpc stub.
     *
     * @param rpcMethod     the RPC method from stub created by {@link #newStubForRPC(Class, String)}}
     * @param input         the input of the RPC method.
     * @param <I>           the input type.
     */
    public <I> void invokeRPC(final RpcDefinition.RpcMethodNoOutput<I> rpcMethod, final I input) {
        rpcMethod.execute(null, input, null, null);
    }

    /**
     * Invoke an RPC method through the rpc stub.
     *
     * @param rpcMethod     the RPC method from stub created by {@link #newStubForRPC(Class, String)}}
     * @return  the output of the RPC execution.
     * @param <O>           the output type.
     */
    public <O> O invokeRPC(final RpcDefinition.RpcMethodNoInput<O> rpcMethod) {
        return rpcMethod.execute(null, null, null);
    }

    /**
     * Invoke an RPC method through the rpc stub.
     *
     * @param rpcMethod     the RPC method from stub created by {@link #newStubForRPC(Class, String)}}
     */
    public void invokeRPC(final RpcDefinition.RpcMethodNoInputNoOutput rpcMethod) {
        rpcMethod.execute(null, null, null);
    }

    private String startProcessInternal(
        final String processType,
        final String processId,
        final Object input,
        final ProcessStartConfig processStartConfig
    ) {
        final Process process = registry.getProcess(processType);

        final ProcessExecutionStartRequest request = new ProcessExecutionStartRequest()
            .namespace(clientOptions.getNamespace())
            .processId(processId)
            .processType(processType)
            .workerUrl(clientOptions.getWorkerUrl())
            .startStateInput(clientOptions.getObjectEncoder().encodeToEncodedObject(input))
            .processStartConfig(
                processStartConfig == null
                    ? null
                    : processStartConfig.toApiModel(
                        process.getPersistenceSchema(),
                        clientOptions.getDatabaseStringEncoder()
                    )
            );

        if (process.getStateSchema() != null && process.getStateSchema().getStartingState() != null) {
            final AsyncState startingState = process.getStateSchema().getStartingState();
            request
                .startStateId(ProcessUtil.getStateId(startingState))
                .startStateConfig(ProcessUtil.getAsyncStateConfig(startingState, process));
        }

        return basicClient.startProcess(request);
    }

    /**
     * Publish message(s) to the local queue for consumption by the process execution.
     *
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param messages      messages.
     */
    private void publishToLocalQueue(final String processId, final LocalQueueMessage... messages) {
        final PublishToLocalQueueRequest request = new PublishToLocalQueueRequest()
            .namespace(clientOptions.getNamespace())
            .processId(processId)
            .messages(Arrays.stream(messages).collect(Collectors.toList()));

        basicClient.publishToLocalQueue(request);
    }
}
