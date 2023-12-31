package integ.publish_to_local_queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import integ.TestUtils;
import integ.spring.IntegConfig;
import integ.spring.WorkerServiceForTesting;
import io.xcherry.core.client.Client;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPublishToLocalQueueProcess {

    public static final String QUEUE_1 = "QUEUE_1";
    public static final String PAYLOAD_1 = "PAYLOAD_1";
    public static final String PAYLOAD_1_2 = "PAYLOAD_1_2";

    public static final String QUEUE_2 = "QUEUE_2";
    public static final int PAYLOAD_2 = 2;

    public static final String QUEUE_3 = "QUEUE_3";

    public static final String DEDUP_ID = "6726c532-71a5-11ee-9e60-acde48001122";

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testPublishToLocalQueueProcess() {
        final Client client = IntegConfig.client;

        final String processId = "publish-to-local-queue-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(PublishToLocalQueueProcess.class, processId, null);
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        // to trigger PublishToLocalQueueStartingState.execute
        client.publishToLocalQueue(processId, QUEUE_1, PAYLOAD_1_2);
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response2.getStatus());

        // to publish a message with duplicated dedupId, nothing changes
        client.publishToLocalQueue(processId, QUEUE_1, DEDUP_ID, null);
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response3 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response3.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response3.getStatus());

        // to complete the execution
        client.publishToLocalQueue(processId, QUEUE_1);
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response4 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response4.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response4.getStatus());
    }
}
