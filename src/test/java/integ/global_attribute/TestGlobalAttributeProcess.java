package integ.global_attribute;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import integ.TestUtils;
import integ.spring.WorkerServiceForTesting;
import integ.spring.XdbConfig;
import io.xdb.core.client.Client;
import io.xdb.core.exception.XDBHttpException;
import io.xdb.gen.models.ProcessExecutionDescribeResponse;
import io.xdb.gen.models.ProcessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGlobalAttributeProcess {

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testGlobalAttributeProcess() {
        final Client client = XdbConfig.client;

        final String processId = "global-attribute-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(GlobalAttributeProcess.class, processId, null);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals("GlobalAttributeProcess", response.getProcessType());
        assertEquals(ProcessStatus.COMPLETED, response.getStatus());

        // fail when trying to insert the same global attributes with the RETURN_ERROR_ON_CONFLICT write mode
        assertThrows(
            XDBHttpException.class,
            () ->
                client.startProcess(
                    GlobalAttributeProcess.class,
                    "global-attribute-process-" + System.currentTimeMillis() / 1000,
                    null
                )
        );
    }
}
