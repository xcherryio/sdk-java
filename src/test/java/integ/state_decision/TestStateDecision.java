package integ.state_decision;

import static integ.spring.WorkerForTesting.WORKER_PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import integ.TestUtils;
import integ.spring.IntegConfig;
import integ.spring.WorkerServiceForTesting;
import io.xcherry.core.client.Client;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestStateDecision {

    public static final Integer INPUT = 11;

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testGracefulCompleteProcess() {
        final Client client = IntegConfig.client;

        final String processId = "graceful-complete-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(GracefulCompleteProcess.class, processId, INPUT);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals("GracefulCompleteProcess", response.getProcessType());
        assertEquals("http://localhost:" + WORKER_PORT, response.getWorkerUrl());
        assertEquals(ProcessStatus.COMPLETED, response.getStatus());
    }

    @Test
    public void testForceCompleteProcess() {
        final Client client = IntegConfig.client;

        final String processId = "force-complete-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(ForceCompleteProcess.class, processId, INPUT);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals("ForceCompleteProcess", response.getProcessType());
        assertEquals("http://localhost:" + WORKER_PORT, response.getWorkerUrl());
        assertEquals(ProcessStatus.COMPLETED, response.getStatus());
    }

    @Test
    public void testForceFailProcess() {
        final Client client = IntegConfig.client;

        final String processId = "force-fail-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(ForceFailProcess.class, processId, INPUT);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals("ForceFailProcess", response.getProcessType());
        assertEquals("http://localhost:" + WORKER_PORT, response.getWorkerUrl());
        assertEquals(ProcessStatus.FAILED, response.getStatus());
    }
}
