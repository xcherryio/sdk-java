package integ.rpc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import integ.TestUtils;
import integ.spring.IntegConfig;
import integ.spring.WorkerServiceForTesting;
import io.xcherry.core.client.Client;
import io.xcherry.core.persistence.selector.app_database.AppDatabaseColumnValueSelector;
import io.xcherry.core.persistence.selector.app_database.AppDatabasePrimaryKeySelector;
import io.xcherry.core.persistence.selector.app_database.AppDatabaseRowSelector;
import io.xcherry.core.persistence.selector.app_database.AppDatabaseSelector;
import io.xcherry.core.process.ProcessStartConfig;
import io.xcherry.gen.models.ProcessExecutionDescribeResponse;
import io.xcherry.gen.models.ProcessStatus;
import io.xcherry.gen.models.WriteConflictMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestRpcProcessWithPersistence {

    public static final String SAMPLE_USER_TABLE = "sample_user_table";

    public static final String USER_ID_1 = "user_id_1";
    public static final String USER_ID_2 = "user_id_2";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String CREATE_TIMESTAMP = "create_timestamp";

    public static final String VAL_USER_ID_1_1 = "rpc_user_id_1_1";
    public static final String VAL_USER_ID_2_1 = "rpc_user_id_2_1";

    public static final String VAL_USER_ID_1_2 = "rpc_user_id_1_2";
    public static final String VAL_USER_ID_2_2 = "rpc_user_id_2_2";

    public static final String VAL_USER_ID_1_3 = "rpc_user_id_1_3";
    public static final String VAL_USER_ID_2_3 = "rpc_user_id_2_3";

    public static final String[] VAL_USER_ID_1S = { VAL_USER_ID_1_1, VAL_USER_ID_1_2, VAL_USER_ID_1_3 };
    public static final String[] VAL_USER_ID_2S = { VAL_USER_ID_2_1, VAL_USER_ID_2_2, VAL_USER_ID_2_3 };

    public static final String VAL_FIRST_NAME_1 = "rpc_first_name_1";
    public static final String VAL_LAST_NAME_1 = "rpc_last_name_1";
    public static final int VAL_CREATE_TIMESTAMP_1 = 111;
    public static final int VAL_CREATE_TIMESTAMP_2 = 222;

    @BeforeEach
    public void setup() {
        WorkerServiceForTesting.startWorkerIfNotUp();
    }

    @Test
    public void testRpcProcessWithPersistence_triggerNextStateDefault() {
        final Client client = IntegConfig.client;

        final String processId = "rpc-persistence-triggerNextStateDefault-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(
            RpcProcessWithPersistence.class,
            processId,
            null,
            getProcessStartConfig(0)
        );
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        final RpcProcessWithPersistence rpcProcessStub = client.newStubForRPC(
            RpcProcessWithPersistence.class,
            processId
        );
        client.invokeRPC(rpcProcessStub::triggerNextStateDefault);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    @Test
    public void testRpcProcessWithPersistence_triggerNextState1() {
        final Client client = IntegConfig.client;

        final String processId = "rpc-persistence-triggerNextState1-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(
            RpcProcessWithPersistence.class,
            processId,
            null,
            getProcessStartConfig(1)
        );
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        final RpcProcessWithPersistence rpcProcessStub = client.newStubForRPC(
            RpcProcessWithPersistence.class,
            processId
        );
        client.invokeRPC(rpcProcessStub::triggerNextState1);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    @Test
    public void testRpcProcessWithPersistence_triggerNextState2() {
        final Client client = IntegConfig.client;

        final String processId = "rpc-persistence-triggerNextState2-" + System.currentTimeMillis() / 1000;

        final String processExecutionId = client.startProcess(
            RpcProcessWithPersistence.class,
            processId,
            null,
            getProcessStartConfig(2)
        );
        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response.getProcessExecutionId());
        assertEquals(ProcessStatus.RUNNING, response.getStatus());

        final RpcProcessWithPersistence rpcProcessStub = client.newStubForRPC(
            RpcProcessWithPersistence.class,
            processId
        );
        client.invokeRPC(rpcProcessStub::triggerNextState2);

        TestUtils.sleep(2);

        final ProcessExecutionDescribeResponse response2 = client.describeCurrentProcessExecution(processId);
        assertEquals(processExecutionId, response2.getProcessExecutionId());
        assertEquals(ProcessStatus.COMPLETED, response2.getStatus());
    }

    private ProcessStartConfig getProcessStartConfig(final int index) {
        return ProcessStartConfig
            .builder()
            .appDatabaseSelector(
                AppDatabaseSelector.create(
                    AppDatabaseRowSelector.create(
                        SAMPLE_USER_TABLE,
                        WriteConflictMode.RETURN_ERROR_ON_CONFLICT,
                        AppDatabasePrimaryKeySelector.create(
                            AppDatabaseColumnValueSelector.create(USER_ID_1, VAL_USER_ID_1S[index]),
                            AppDatabaseColumnValueSelector.create(USER_ID_2, VAL_USER_ID_2S[index])
                        ),
                        AppDatabaseColumnValueSelector.create(FIRST_NAME, VAL_FIRST_NAME_1),
                        AppDatabaseColumnValueSelector.create(LAST_NAME, VAL_LAST_NAME_1),
                        AppDatabaseColumnValueSelector.create(CREATE_TIMESTAMP, VAL_CREATE_TIMESTAMP_1)
                    )
                )
            )
            .build();
    }
}
