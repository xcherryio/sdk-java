package integ.rpc;

import static integ.rpc.RpcProcessWithPersistence.RPC_PERSISTENCE_READ_REQUEST_1;
import static integ.rpc.RpcProcessWithPersistence.RPC_PERSISTENCE_READ_REQUEST_2;
import static integ.rpc.TestRpcProcessWithPersistence.CREATE_TIMESTAMP;
import static integ.rpc.TestRpcProcessWithPersistence.FIRST_NAME;
import static integ.rpc.TestRpcProcessWithPersistence.LAST_NAME;
import static integ.rpc.TestRpcProcessWithPersistence.SAMPLE_USER_TABLE;
import static integ.rpc.TestRpcProcessWithPersistence.USER_ID_1;
import static integ.rpc.TestRpcProcessWithPersistence.USER_ID_2;
import static integ.rpc.TestRpcProcessWithPersistence.VAL_CREATE_TIMESTAMP_1;
import static integ.rpc.TestRpcProcessWithPersistence.VAL_CREATE_TIMESTAMP_2;
import static integ.rpc.TestRpcProcessWithPersistence.VAL_FIRST_NAME_1;
import static integ.rpc.TestRpcProcessWithPersistence.VAL_LAST_NAME_1;
import static integ.rpc.TestRpcProcessWithPersistence.VAL_USER_ID_1S;
import static integ.rpc.TestRpcProcessWithPersistence.VAL_USER_ID_2S;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.exception.persistence.AppDatabaseNotFoundException;
import io.xcherry.core.persistence.AppDatabaseRow;
import io.xcherry.core.persistence.Persistence;
import io.xcherry.core.persistence.readrequest.AppDatabaseReadRequest;
import io.xcherry.core.persistence.readrequest.AppDatabaseTableReadRequest;
import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.core.persistence.schema.appdatabase.AppDatabaseColumnSchema;
import io.xcherry.core.persistence.schema.appdatabase.AppDatabasePrimaryKeySchema;
import io.xcherry.core.persistence.schema.appdatabase.AppDatabaseSchema;
import io.xcherry.core.persistence.schema.appdatabase.AppDatabaseTableSchema;
import io.xcherry.core.persistence.schema.localattribute.LocalAttributeSchema;
import io.xcherry.core.process.Process;
import io.xcherry.core.rpc.RPC;
import io.xcherry.core.rpc.RpcPersistenceReadRequest;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.AsyncStateOptions;
import io.xcherry.core.state.StateDecision;
import io.xcherry.core.state.StateSchema;
import io.xcherry.gen.models.DatabaseLockingType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RpcProcessWithPersistence implements Process {

    public static final String RPC_PERSISTENCE_READ_REQUEST_1 = "RPC_PERSISTENCE_READ_REQUEST_1";
    public static final String RPC_PERSISTENCE_READ_REQUEST_2 = "RPC_PERSISTENCE_READ_REQUEST_2";

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(
            new RpcProcessWithPersistenceStartingState(),
            new RpcProcessWithPersistenceNextState1()
        );
    }

    @Override
    public PersistenceSchema getPersistenceSchema() {
        return PersistenceSchema.define(
            AppDatabaseSchema.define(
                AppDatabaseTableSchema.define(
                    SAMPLE_USER_TABLE,
                    DatabaseLockingType.NO_LOCKING,
                    AppDatabasePrimaryKeySchema.define(
                        AppDatabaseColumnSchema.define(USER_ID_1, String.class),
                        AppDatabaseColumnSchema.define(USER_ID_2, String.class)
                    ),
                    AppDatabaseColumnSchema.define(FIRST_NAME, String.class, true),
                    AppDatabaseColumnSchema.define(LAST_NAME, String.class, true),
                    AppDatabaseColumnSchema.define(CREATE_TIMESTAMP, Integer.class, false)
                )
            ),
            LocalAttributeSchema.EMPTY(),
            RpcPersistenceReadRequest.create(
                RPC_PERSISTENCE_READ_REQUEST_1,
                AppDatabaseReadRequest.create(
                    // Primary key columns will always be loaded.
                    AppDatabaseTableReadRequest.create(
                        SAMPLE_USER_TABLE,
                        DatabaseLockingType.NO_LOCKING,
                        FIRST_NAME,
                        LAST_NAME,
                        CREATE_TIMESTAMP
                    )
                )
            ),
            RpcPersistenceReadRequest.create(RPC_PERSISTENCE_READ_REQUEST_2, AppDatabaseReadRequest.create())
        );
    }

    @RPC
    public void triggerNextStateDefault(
        final Context context,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("triggerNextStateDefault");

        final List<AppDatabaseRow> userTableRows = persistence.getAppDatabaseRows(SAMPLE_USER_TABLE);
        assertEquals(1, userTableRows.size());

        assertEquals(VAL_USER_ID_1S[0], userTableRows.get(0).getColumnValue(USER_ID_1));
        assertEquals(VAL_USER_ID_2S[0], userTableRows.get(0).getColumnValue(USER_ID_2));
        assertEquals(VAL_FIRST_NAME_1, userTableRows.get(0).getColumnValue(FIRST_NAME));
        assertEquals(VAL_LAST_NAME_1, userTableRows.get(0).getColumnValue(LAST_NAME));
        assertThrows(AppDatabaseNotFoundException.class, () -> userTableRows.get(0).getColumnValue(CREATE_TIMESTAMP));

        // upsert
        userTableRows.get(0).upsertColumn(CREATE_TIMESTAMP, VAL_CREATE_TIMESTAMP_2);

        communication.triggerSingleStateMovement("RpcProcessWithPersistenceNextState1", "");
    }

    @RPC(rpcPersistenceReadRequestName = RPC_PERSISTENCE_READ_REQUEST_1)
    public void triggerNextState1(
        final Context context,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("triggerNextState1");

        final List<AppDatabaseRow> userTableRows = persistence.getAppDatabaseRows(SAMPLE_USER_TABLE);
        assertEquals(1, userTableRows.size());

        assertEquals(VAL_USER_ID_1S[1], userTableRows.get(0).getColumnValue(USER_ID_1));
        assertEquals(VAL_USER_ID_2S[1], userTableRows.get(0).getColumnValue(USER_ID_2));
        assertEquals(VAL_FIRST_NAME_1, userTableRows.get(0).getColumnValue(FIRST_NAME));
        assertEquals(VAL_LAST_NAME_1, userTableRows.get(0).getColumnValue(LAST_NAME));
        assertEquals(VAL_CREATE_TIMESTAMP_1, userTableRows.get(0).getColumnValue(CREATE_TIMESTAMP));

        // upsert
        userTableRows.get(0).upsertColumn(CREATE_TIMESTAMP, VAL_CREATE_TIMESTAMP_2);

        communication.triggerSingleStateMovement("RpcProcessWithPersistenceNextState1", RPC_PERSISTENCE_READ_REQUEST_1);
    }

    @RPC(rpcPersistenceReadRequestName = RPC_PERSISTENCE_READ_REQUEST_2)
    public void triggerNextState2(
        final Context context,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("triggerNextState2");

        assertThrows(AppDatabaseNotFoundException.class, () -> persistence.getAppDatabaseRows(SAMPLE_USER_TABLE));

        communication.triggerSingleStateMovement("RpcProcessWithPersistenceNextState1", RPC_PERSISTENCE_READ_REQUEST_2);
    }
}

class RpcProcessWithPersistenceStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Void input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("RpcProcessWithPersistenceStartingState.execute: " + input);

        return StateDecision.deadEnd();
    }
}

class RpcProcessWithPersistenceNextState1 implements AsyncState<String> {

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions
            .builder(RpcProcessWithPersistenceNextState1.class)
            .appDatabaseReadRequest(
                AppDatabaseReadRequest.create(
                    // Primary key columns will always be loaded.
                    AppDatabaseTableReadRequest.create(
                        SAMPLE_USER_TABLE,
                        DatabaseLockingType.NO_LOCKING,
                        FIRST_NAME,
                        LAST_NAME,
                        CREATE_TIMESTAMP
                    )
                )
            )
            .build();
    }

    @Override
    public StateDecision execute(
        final Context context,
        final String input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("RpcProcessWithPersistenceNextState1.execute: " + input);

        final List<AppDatabaseRow> userTableRows = persistence.getAppDatabaseRows(SAMPLE_USER_TABLE);
        assertEquals(1, userTableRows.size());

        switch (input) {
            case RPC_PERSISTENCE_READ_REQUEST_1:
                assertEquals(VAL_USER_ID_1S[1], userTableRows.get(0).getColumnValue(USER_ID_1));
                assertEquals(VAL_USER_ID_2S[1], userTableRows.get(0).getColumnValue(USER_ID_2));
                assertEquals(VAL_FIRST_NAME_1, userTableRows.get(0).getColumnValue(FIRST_NAME));
                assertEquals(VAL_LAST_NAME_1, userTableRows.get(0).getColumnValue(LAST_NAME));
                assertEquals(VAL_CREATE_TIMESTAMP_2, userTableRows.get(0).getColumnValue(CREATE_TIMESTAMP));
                break;
            case RPC_PERSISTENCE_READ_REQUEST_2:
                assertEquals(VAL_USER_ID_1S[2], userTableRows.get(0).getColumnValue(USER_ID_1));
                assertEquals(VAL_USER_ID_2S[2], userTableRows.get(0).getColumnValue(USER_ID_2));
                assertEquals(VAL_FIRST_NAME_1, userTableRows.get(0).getColumnValue(FIRST_NAME));
                assertEquals(VAL_LAST_NAME_1, userTableRows.get(0).getColumnValue(LAST_NAME));
                assertEquals(VAL_CREATE_TIMESTAMP_1, userTableRows.get(0).getColumnValue(CREATE_TIMESTAMP));
                break;
            default:
                assertEquals(VAL_USER_ID_1S[0], userTableRows.get(0).getColumnValue(USER_ID_1));
                assertEquals(VAL_USER_ID_2S[0], userTableRows.get(0).getColumnValue(USER_ID_2));
                assertEquals(VAL_FIRST_NAME_1, userTableRows.get(0).getColumnValue(FIRST_NAME));
                assertEquals(VAL_LAST_NAME_1, userTableRows.get(0).getColumnValue(LAST_NAME));
                assertEquals(VAL_CREATE_TIMESTAMP_2, userTableRows.get(0).getColumnValue(CREATE_TIMESTAMP));
        }

        return StateDecision.gracefulCompleteProcess();
    }
}
