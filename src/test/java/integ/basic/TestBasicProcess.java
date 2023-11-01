package integ.basic;

import static integ.basic.BasicProcess.INPUT;
import static integ.spring.WorkerForTesting.WORKER_PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import integ.TestUtils;
import integ.spring.WorkerServiceForTesting;
import integ.spring.XdbConfig;
import io.xdb.core.client.Client;
import io.xdb.gen.models.ProcessExecutionDescribeResponse;
import io.xdb.gen.models.ProcessExecutionStopType;
import io.xdb.gen.models.ProcessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestBasicProcess {

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testBasicProcessAndTerminate() {
        final Client client = XdbConfig.client;

        final String processId = "basic-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(BasicProcess.class, processId, INPUT);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals("BasicProcess", response.getProcessType());
        assertEquals("http://localhost:" + WORKER_PORT, response.getWorkerUrl());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        client.stopProcess(processId);
        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.TERMINATED, response2.getStatus());
    }

    @Test
    public void testBasicProcessAndFail() {
        final Client client = XdbConfig.client;

        final String processId = "basic-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(BasicProcess.class, processId, INPUT);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals("BasicProcess", response.getProcessType());
        assertEquals("http://localhost:" + WORKER_PORT, response.getWorkerUrl());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        client.stopProcess(processId, ProcessExecutionStopType.FAIL);
        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.FAILED, response2.getStatus());
    }
}
