package integ.rpc;

import static integ.rpc.RpcProcess.INPUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import integ.TestUtils;
import integ.spring.IntegConfig;
import integ.spring.WorkerServiceForTesting;
import io.xcherry.core.client.Client;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestRpcProcess {

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testRpcProcess_triggerNextState() {
        final Client client = IntegConfig.client;

        final String processId = "rpc-triggerNextState-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(RpcProcess.class, processId, null);
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        final RpcProcess rpcProcessStub = client.newStubForRPC(RpcProcess.class, processId);
        final String output = client.invokeRPC(rpcProcessStub::triggerNextState, INPUT);
        assertEquals(String.valueOf(INPUT + 1), output);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    @Test
    public void testRpcProcess_triggerNextStateNoOutput() {
        final Client client = IntegConfig.client;

        final String processId = "rpc-triggerNextStateNoOutput-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(RpcProcess.class, processId, null);
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        final RpcProcess rpcProcessStub = client.newStubForRPC(RpcProcess.class, processId);
        client.invokeRPC(rpcProcessStub::triggerNextStateNoOutput, INPUT);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    @Test
    public void testRpcProcess_triggerNextStateNoInput() {
        final Client client = IntegConfig.client;

        final String processId = "rpc-triggerNextStateNoInput-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(RpcProcess.class, processId, null);
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        final RpcProcess rpcProcessStub = client.newStubForRPC(RpcProcess.class, processId);
        final String output = client.invokeRPC(rpcProcessStub::triggerNextStateNoInput);
        assertEquals(String.valueOf(INPUT + 2), output);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    @Test
    public void testRpcProcess_triggerNextStateNoInputNoOutput() {
        final Client client = IntegConfig.client;

        final String processId = "rpc-triggerNextStateNoInputNoOutput-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(RpcProcess.class, processId, null);
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        final RpcProcess rpcProcessStub = client.newStubForRPC(RpcProcess.class, processId);
        client.invokeRPC(rpcProcessStub::triggerNextStateNoInputNoOutput);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }
}
