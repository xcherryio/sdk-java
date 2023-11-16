package integ.global_attribute;

//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import com.google.common.collect.ImmutableMap;
//import integ.TestUtils;
import integ.spring.WorkerServiceForTesting;
import org.junit.jupiter.api.BeforeEach;

//import org.junit.jupiter.api.Test;

public class TestGlobalAttributeProcess {

    public static final String TABLE_NAME = "sample_user_table";
    public static final String PK_KEY = "user_id";
    public static final String PK_VALUE = "pk_value";
    public static final String COL_KEY_1 = "first_name";
    public static final String COL_VALUE_1 = "col_1_value";
    public static final String COL_VALUE_1_2 = "col_1_value_2";
    public static final String COL_KEY_2 = "last_name";
    public static final String COL_VALUE_2 = "col_2_value";
    public static final String COL_KEY_3 = "create_timestamp";
    public static final int COL_VALUE_3 = 111;

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
    //                .initializeGlobalAttributes(
    //                    TABLE_NAME,
    //                    ImmutableMap.of(PK_KEY, PK_VALUE),
    //                    ImmutableMap.of(COL_KEY_1, COL_VALUE_1, COL_KEY_2, COL_VALUE_2),
    //                    AttributeWriteConflictMode.RETURN_ERROR_ON_CONFLICT
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
