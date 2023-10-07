package io.xdb.core.client;

import io.xdb.core.process.Process;
import io.xdb.core.registry.Registry;
import io.xdb.core.state.AsyncState;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.ProcessExecutionDescribeResponse;
import io.xdb.gen.models.ProcessExecutionStartRequest;
import java.time.Duration;

public class Client {

    private final Registry registry;
    private final ClientOptions clientOptions;

    private final BasicClient basicClient;

    public Client(final Registry registry, final ClientOptions clientOptions) {
        this.registry = registry;
        this.clientOptions = clientOptions;
        this.basicClient = new BasicClient(clientOptions);
    }

    public String startProcess(final Process process, final String processId, final Object input) {
        final String processType = process.getOptions().getType(process.getClass());
        return startProcessInternal(processType, processId, input);
    }

    /**
     * Caution: if you intend to override certain process options, utilize the {@link Client#startProcess(Process, String, Object)} method
     *
     * @param processClass
     * @param processId
     * @param input
     * @return
     */
    public String startProcess(
        final Class<? extends Process> processClass,
        final String processId,
        final Object input
    ) {
        final String processType = ProcessUtil.getProcessType(processClass);
        return startProcessInternal(processType, processId, input);
    }

    public ProcessExecutionDescribeResponse describeCurrentProcessExecution(final String processId) {
        return describeCurrentProcessExecution(ProcessUtil.DEFAULT_NAMESPACE, processId);
    }

    public ProcessExecutionDescribeResponse describeCurrentProcessExecution(
        final String namespace,
        final String processId
    ) {
        return basicClient.describeCurrentProcessExecution(namespace, processId);
    }

    // TODO: placeholder to be used in integration test for now
    public void getProcessResultWithWait(final String processId) {
        System.out.println(processId);
        try {
            Thread.sleep(Duration.ofSeconds(2).toMillis());
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String startProcessInternal(final String processType, final String processId, final Object input) {
        final Process process = registry.getProcess(processType);

        final ProcessExecutionStartRequest request = new ProcessExecutionStartRequest()
            .namespace(process.getOptions().getNamespace())
            .processId(processId)
            .processType(processType)
            .workerUrl(clientOptions.getWorkerUrl())
            .startStateInput(clientOptions.getObjectEncoder().encode(input))
            .processStartConfig(process.getOptions().getProcessStartConfig());

        final AsyncState startingState = process.getStateSchema().getStartingState();
        if (startingState != null) {
            request
                .startStateId(startingState.getOptions().getId(startingState.getClass()))
                .startStateConfig(ProcessUtil.getAsyncStateConfig(startingState));
        }

        return basicClient.startProcess(request);
    }
}
