package integ.app_database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import integ.TestUtils;
import integ.spring.IntegConfig;
import integ.spring.WorkerServiceForTesting;
import io.xcherry.core.client.Client;
import io.xcherry.core.exception.HttpException;
import io.xcherry.core.persistence.selector.appdatabase.AppDatabaseColumnValueSelector;
import io.xcherry.core.persistence.selector.appdatabase.AppDatabasePrimaryKeySelector;
import io.xcherry.core.persistence.selector.appdatabase.AppDatabaseRowSelector;
import io.xcherry.core.persistence.selector.appdatabase.AppDatabaseSelector;
import io.xcherry.core.process.ProcessStartConfig;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessStatus;
import io.xcherry.gen.models.WriteConflictMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestAppDatabaseProcess {

    public static final String SAMPLE_USER_TABLE = "sample_user_table";

    public static final String USER_ID_1 = "user_id_1";
    public static final String USER_ID_2 = "user_id_2";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String CREATE_TIMESTAMP = "create_timestamp";

    public static final String VAL_USER_ID_1_1 = "user_id_1_1";
    public static final String VAL_USER_ID_2_1 = "user_id_2_1";
    public static final String VAL_FIRST_NAME_1 = "first_name_1";
    public static final String VAL_LAST_NAME_1 = "last_name_1";
    public static final int VAL_CREATE_TIMESTAMP_1 = 111;

    public static final String VAL_USER_ID_1_2 = "user_id_1_2";
    public static final String VAL_USER_ID_2_2 = "user_id_2_2";
    public static final String VAL_FIRST_NAME_2 = "first_name_2";
    public static final String VAL_LAST_NAME_2 = "last_name_2";
    public static final String VAL_LAST_NAME_2_2 = "last_name_2_2";
    public static final int VAL_CREATE_TIMESTAMP_2 = 222;

    public static final String SAMPLE_ORDER_TABLE = "sample_order_table";
    public static final String ORDER_ID = "order_id";
    public static final String USER_ID = "user_id";

    public static final int VAL_ORDER_ID = 1;
    public static final int VAL_USER_ID = 1;
    public static final int VAL_USER_ID_2 = 1;

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testAppDatabaseProcess() {
        final Client client = IntegConfig.client;

        final ProcessStartConfig processStartConfig = ProcessStartConfig
            .builder()
            .appDatabaseSelector(
                AppDatabaseSelector.create(
                    AppDatabaseRowSelector.create(
                        SAMPLE_USER_TABLE,
                        WriteConflictMode.RETURN_ERROR_ON_CONFLICT,
                        AppDatabasePrimaryKeySelector.create(
                            AppDatabaseColumnValueSelector.create(USER_ID_1, VAL_USER_ID_1_1),
                            AppDatabaseColumnValueSelector.create(USER_ID_2, VAL_USER_ID_2_1)
                        ),
                        AppDatabaseColumnValueSelector.create(FIRST_NAME, VAL_FIRST_NAME_1),
                        AppDatabaseColumnValueSelector.create(LAST_NAME, VAL_LAST_NAME_1)
                    ),
                    AppDatabaseRowSelector.create(
                        SAMPLE_USER_TABLE,
                        WriteConflictMode.RETURN_ERROR_ON_CONFLICT,
                        AppDatabasePrimaryKeySelector.create(
                            AppDatabaseColumnValueSelector.create(USER_ID_1, VAL_USER_ID_1_2),
                            AppDatabaseColumnValueSelector.create(USER_ID_2, VAL_USER_ID_2_2)
                        ),
                        AppDatabaseColumnValueSelector.create(FIRST_NAME, VAL_FIRST_NAME_2),
                        AppDatabaseColumnValueSelector.create(LAST_NAME, VAL_LAST_NAME_2)
                    ),
                    AppDatabaseRowSelector.create(
                        SAMPLE_ORDER_TABLE,
                        WriteConflictMode.RETURN_ERROR_ON_CONFLICT,
                        AppDatabasePrimaryKeySelector.create(
                            AppDatabaseColumnValueSelector.create(ORDER_ID, VAL_ORDER_ID)
                        ),
                        AppDatabaseColumnValueSelector.create(USER_ID, VAL_USER_ID)
                    )
                )
            )
            .build();

        final String processId = "app-database-process-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(
            AppDatabaseProcess.class,
            processId,
            null,
            processStartConfig
        );

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals("AppDatabaseProcess", response.getProcessType());
        assertEquals(ProcessStatus.COMPLETED, response.getStatus());

        // fail when trying to insert the same app database rows with the RETURN_ERROR_ON_CONFLICT write mode
        assertThrows(
            HttpException.class,
            () ->
                client.startProcess(
                    AppDatabaseProcess.class,
                    "app-database-process-" + System.currentTimeMillis() / 1000,
                    null,
                    processStartConfig
                )
        );
    }
}
