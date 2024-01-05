package integ.local_attribute;

import static org.junit.jupiter.api.Assertions.assertEquals;

import integ.TestUtils;
import integ.spring.IntegConfig;
import integ.spring.WorkerServiceForTesting;
import io.xcherry.core.client.Client;
import io.xcherry.core.persistence.selector.localattribute.LocalAttributeKeyValueSelector;
import io.xcherry.core.persistence.selector.localattribute.LocalAttributeSelector;
import io.xcherry.core.process.ProcessStartConfig;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLocalAttributeProcess {

    public static final String LOCAL_ATTRIBUTE_KEY_1 = "LOCAL_ATTRIBUTE_KEY_1";
    public static final String LOCAL_ATTRIBUTE_VALUE_1 = "LOCAL_ATTRIBUTE_VALUE_1";
    public static final String LOCAL_ATTRIBUTE_VALUE_1_2 = "LOCAL_ATTRIBUTE_VALUE_1_2";

    public static final String LOCAL_ATTRIBUTE_KEY_2 = "LOCAL_ATTRIBUTE_KEY_2";
    public static final int LOCAL_ATTRIBUTE_VALUE_2 = 2;
    public static final int LOCAL_ATTRIBUTE_VALUE_2_2 = 3;

    public static final String LOCAL_ATTRIBUTE_KEY_3 = "LOCAL_ATTRIBUTE_KEY_3";
    public static final String LOCAL_ATTRIBUTE_VALUE_3 = "LOCAL_ATTRIBUTE_VALUE_3";

    public static final String LOCAL_ATTRIBUTE_NOT_EXIST = "LOCAL_ATTRIBUTE_NOT_EXIST";

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testLocalAttributeProcess() {
        final Client client = IntegConfig.client;

        final ProcessStartConfig processStartConfig = ProcessStartConfig
            .builder()
            .localAttributeSelector(
                LocalAttributeSelector.create(
                    LocalAttributeKeyValueSelector.create(LOCAL_ATTRIBUTE_KEY_1, LOCAL_ATTRIBUTE_VALUE_1),
                    LocalAttributeKeyValueSelector.create(LOCAL_ATTRIBUTE_KEY_2, LOCAL_ATTRIBUTE_VALUE_2)
                )
            )
            .build();

        final String processId = "local-attribute-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(
            LocalAttributeProcess.class,
            processId,
            null,
            processStartConfig
        );

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals("LocalAttributeProcess", response.getProcessType());
        assertEquals(ProcessStatus.COMPLETED, response.getStatus());
    }
}
