package integ.wait_for_process_completion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import integ.TestUtils;
import integ.spring.IntegConfig;
import integ.spring.WorkerServiceForTesting;
import io.xcherry.core.client.Client;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessExecutionWaitForCompletionResponse;
import io.xcherry.gen.models.ProcessStatus;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestWaitForProcessCompletion {

    public static final String QUEUE_1 = "QUEUE_1";

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testWaitForProcessCompletionTimeout() {
        final Client client = IntegConfig.client;

        final String processId = "wait-for-process-completion-timeout-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(WaitForProcessCompletionProcess.class, processId, null);

        TestUtils.sleep(1);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        // Timeout
        final long startTime = Instant.now().toEpochMilli();
        final ProcessExecutionWaitForCompletionResponse waitForCompletionResponse = client.waitForProcessCompletion(
            processId,
            10
        );
        assertEquals(Boolean.TRUE, waitForCompletionResponse.getTimeout());
        assertTrue(Instant.now().toEpochMilli() - startTime >= 10 * 1000);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response2.getStatus());
    }

    @Test
    public void testWaitForProcessCompletionStatus() {
        final Client client = IntegConfig.client;

        final String processId = "wait-for-process-completion-status-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(WaitForProcessCompletionProcess.class, processId, null);

        TestUtils.sleep(1);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        // Call the waitForProcessCompletion first, then complete the process and let the waitForProcessCompletion response return with the correct status
        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        final Callable<ProcessExecutionWaitForCompletionResponse> waitForProcessCompletion = () ->
            client.waitForProcessCompletion(processId, 10);

        final Runnable completeProcess = () -> client.publishToLocalQueue(processId, QUEUE_1);

        final Future<ProcessExecutionWaitForCompletionResponse> waitForProcessCompletionFeature = executorService.submit(
            waitForProcessCompletion
        );
        executorService.submit(completeProcess);

        try {
            final ProcessExecutionWaitForCompletionResponse waitForCompletionResponse = waitForProcessCompletionFeature.get();
            assertEquals(ProcessStatus.COMPLETED, waitForCompletionResponse.getStatus());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    @Test
    public void testWaitForProcessCompletionMultipleRequests() {
        final Client client = IntegConfig.client;

        final String processId = "wait-for-process-completion-multi-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(WaitForProcessCompletionProcess.class, processId, null);

        TestUtils.sleep(1);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        // Call the waitForProcessCompletion twice first, then complete the process and let the waitForProcessCompletion responses return with the correct status
        final ExecutorService executorService = Executors.newFixedThreadPool(3);

        final Callable<ProcessExecutionWaitForCompletionResponse> waitForProcessCompletion = () ->
            client.waitForProcessCompletion(processId, 10);

        final Runnable completeProcess = () -> client.publishToLocalQueue(processId, QUEUE_1);

        final Future<ProcessExecutionWaitForCompletionResponse> waitForProcessCompletionFeature1 = executorService.submit(
            waitForProcessCompletion
        );
        final Future<ProcessExecutionWaitForCompletionResponse> waitForProcessCompletionFeature2 = executorService.submit(
            waitForProcessCompletion
        );
        executorService.submit(completeProcess);

        try {
            final ProcessExecutionWaitForCompletionResponse waitForCompletionResponse1 = waitForProcessCompletionFeature1.get();
            assertEquals(ProcessStatus.COMPLETED, waitForCompletionResponse1.getStatus());

            final ProcessExecutionWaitForCompletionResponse waitForCompletionResponse2 = waitForProcessCompletionFeature2.get();
            assertEquals(ProcessStatus.COMPLETED, waitForCompletionResponse2.getStatus());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }
}
