package integ.id_reuse_policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import integ.TestUtils;
import integ.spring.WorkerServiceForTesting;
import integ.spring.XdbConfig;
import io.xdb.core.client.Client;
import io.xdb.core.exception.status.ProcessAlreadyStartedException;
import io.xdb.gen.models.ProcessExecutionDescribeResponse;
import io.xdb.gen.models.ProcessExecutionStopType;
import io.xdb.gen.models.ProcessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestIdReusePolicy {

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testAllowIfNoRunningProcess() {
        final Client client = XdbConfig.client;

        final String processId = "allow-if-no-running-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(AllowIfNoRunningProcess.class, processId, null);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        assertThrows(
            ProcessAlreadyStartedException.class,
            () -> client.startProcess(AllowIfNoRunningProcess.class, processId, null)
        );

        client.stopProcess(processId);

        final String newProcessExecutionId = client.startProcess(AllowIfNoRunningProcess.class, processId, null);
        assertNotEquals(processExecutionId, newProcessExecutionId);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.RUNNING, response2.getStatus());
        assertEquals(newProcessExecutionId, response2.getProcessExecutionId());

        client.stopProcess(processId);
    }

    @Test
    public void testAllowIfPreviousExitAbnormallyProcessWhenProcessCompleted() {
        final Client client = XdbConfig.client;

        final String processId =
            "allow-if-previous-exit-abnormally-process-completed-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(
            new AllowIfPreviousExitAbnormallyProcess(),
            processId,
            null
        );

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.COMPLETED, response.getStatus());

        assertThrows(
            ProcessAlreadyStartedException.class,
            () -> client.startProcess(new AllowIfPreviousExitAbnormallyProcess(), processId, null)
        );
    }

    @Test
    public void testAllowIfPreviousExitAbnormallyProcessWhenProcessFailed() {
        final Client client = XdbConfig.client;

        final String processId =
            "allow-if-previous-exit-abnormally-process-failed-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(
            new AllowIfPreviousExitAbnormallyProcess(),
            processId,
            null
        );

        client.stopProcess(processId, ProcessExecutionStopType.FAIL);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.FAILED, response.getStatus());

        final String newProcessExecutionId = client.startProcess(
            new AllowIfPreviousExitAbnormallyProcess(),
            processId,
            null
        );
        assertNotEquals(processExecutionId, newProcessExecutionId);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.RUNNING, response2.getStatus());
        assertEquals(newProcessExecutionId, response2.getProcessExecutionId());

        client.stopProcess(processId);
    }

    @Test
    public void testDisallowReuseProcess() {
        final Client client = XdbConfig.client;

        final String processId = "disallow-reuse-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(new DisallowReuseProcess(), processId, null);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        assertThrows(
            ProcessAlreadyStartedException.class,
            () -> client.startProcess(new DisallowReuseProcess(), processId, null)
        );

        client.stopProcess(processId);

        assertThrows(
            ProcessAlreadyStartedException.class,
            () -> client.startProcess(new DisallowReuseProcess(), processId, null)
        );
    }

    @Test
    public void testTerminateIfRunningProcess() {
        final Client client = XdbConfig.client;

        final String processId = "terminate-if-running-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(new TerminateIfRunningProcess(), processId, null);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        final String newProcessExecutionId = client.startProcess(new TerminateIfRunningProcess(), processId, null);

        assertNotEquals(processExecutionId, newProcessExecutionId);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(ProcessStatus.RUNNING, response2.getStatus());
        assertEquals(newProcessExecutionId, response2.getProcessExecutionId());

        client.stopProcess(processId);
    }
}
