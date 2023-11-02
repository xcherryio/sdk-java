package integ.command_request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import integ.TestUtils;
import integ.spring.WorkerServiceForTesting;
import integ.spring.XdbConfig;
import io.xdb.core.client.Client;
import io.xdb.gen.models.ProcessExecutionDescribeResponse;
import io.xdb.gen.models.ProcessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestCommandRequest {

    public static final String QUEUE_1 = "QUEUE_1";

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testCommandRequestAnyCompleteTimer() {
        final Client client = XdbConfig.client;

        final String processId = "command-request-any-timer-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(CommandRequestAnyCompleteProcess.class, processId, null);

        TestUtils.sleep(5);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        TestUtils.sleep(7);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    @Test
    public void testCommandRequestAnyCompleteLocalQueue() {
        final Client client = XdbConfig.client;

        final String processId = "command-request-any-local-queue-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(CommandRequestAnyCompleteProcess.class, processId, null);

        TestUtils.sleep(5);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        client.publishToLocalQueue(processId, QUEUE_1);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    @Test
    public void testCommandRequestAllComplete() {
        final Client client = XdbConfig.client;

        final String processId = "command-request-all-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(CommandRequestAllCompleteProcess.class, processId, null);

        TestUtils.sleep(13);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        client.publishToLocalQueue(processId, QUEUE_1);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }
}
