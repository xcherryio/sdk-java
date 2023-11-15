package integ.global_attribute;

import integ.spring.WorkerServiceForTesting;
import org.junit.jupiter.api.BeforeEach;

public class TestGlobalAttributeProcess {

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }
    // TODO

    //    @Test
    //    public void testGlobalAttributeProcess() {
    //        final Client client = XdbConfig.client;
    //
    //        final String processId = "global-attribute-process-" + System.currentTimeMillis() / 1000;
    //
    //        final String processExecutionId = client.startProcess(
    //            GlobalAttributeProcess.class,
    //            processId,
    //            null,
    //            ProcessStartConfig
    //                .builder()
    //                .build()
    //                .addGlobalAttributesToUpsert(
    //                    PersistenceTableRowToUpsert
    //                        .create(TABLE_NAME, AttributeWriteConflictMode.RETURN_ERROR_ON_CONFLICT)
    //                        .addPrimaryKeyColumn(PK_KEY, PK_VALUE)
    //                        .addNonPrimaryKeyColumn(COL_KEY_1, COL_VALUE_1)
    //                        .addNonPrimaryKeyColumn(COL_KEY_2, COL_VALUE_2)
    //                )
    //        );
    //
    //        TestUtils.sleep(2);
    //
    //        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
    //        assertEquals(processExecutionId, response.getProcessExecutionId());
    //        assertEquals("GlobalAttributeProcess", response.getProcessType());
    //        assertEquals(ProcessStatus.COMPLETED, response.getStatus());
    //
    //        // fail when trying to insert the same global attributes with the RETURN_ERROR_ON_CONFLICT write mode
    //        assertThrows(
    //            XDBHttpException.class,
    //            () ->
    //                client.startProcess(
    //                    GlobalAttributeProcess.class,
    //                    "global-attribute-process-" + System.currentTimeMillis() / 1000,
    //                    null
    //                )
    //        );
    //    }
}
