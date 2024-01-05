package integ.app_database;

import static integ.app_database.TestAppDatabaseProcess.CREATE_TIMESTAMP;
import static integ.app_database.TestAppDatabaseProcess.FIRST_NAME;
import static integ.app_database.TestAppDatabaseProcess.LAST_NAME;
import static integ.app_database.TestAppDatabaseProcess.ORDER_ID;
import static integ.app_database.TestAppDatabaseProcess.SAMPLE_ORDER_TABLE;
import static integ.app_database.TestAppDatabaseProcess.SAMPLE_USER_TABLE;
import static integ.app_database.TestAppDatabaseProcess.USER_ID;
import static integ.app_database.TestAppDatabaseProcess.USER_ID_1;
import static integ.app_database.TestAppDatabaseProcess.USER_ID_2;
import static integ.app_database.TestAppDatabaseProcess.VAL_CREATE_TIMESTAMP_1;
import static integ.app_database.TestAppDatabaseProcess.VAL_CREATE_TIMESTAMP_2;
import static integ.app_database.TestAppDatabaseProcess.VAL_FIRST_NAME_1;
import static integ.app_database.TestAppDatabaseProcess.VAL_FIRST_NAME_2;
import static integ.app_database.TestAppDatabaseProcess.VAL_LAST_NAME_1;
import static integ.app_database.TestAppDatabaseProcess.VAL_LAST_NAME_2;
import static integ.app_database.TestAppDatabaseProcess.VAL_LAST_NAME_2_2;
import static integ.app_database.TestAppDatabaseProcess.VAL_ORDER_ID;
import static integ.app_database.TestAppDatabaseProcess.VAL_USER_ID;
import static integ.app_database.TestAppDatabaseProcess.VAL_USER_ID_1_1;
import static integ.app_database.TestAppDatabaseProcess.VAL_USER_ID_1_2;
import static integ.app_database.TestAppDatabaseProcess.VAL_USER_ID_2;
import static integ.app_database.TestAppDatabaseProcess.VAL_USER_ID_2_1;
import static integ.app_database.TestAppDatabaseProcess.VAL_USER_ID_2_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.xcherry.core.command.CommandRequest;
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
import io.xcherry.core.process.Process;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.AsyncStateOptions;
import io.xcherry.core.state.StateDecision;
import io.xcherry.core.state.StateSchema;
import io.xcherry.gen.models.DatabaseLockingType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AppDatabaseProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(
            new AppDatabaseProcessStartingState(),
            new AppDatabaseProcessNextState1(),
            new AppDatabaseProcessNextState2()
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
                ),
                AppDatabaseTableSchema.define(
                    SAMPLE_ORDER_TABLE,
                    DatabaseLockingType.NO_LOCKING,
                    AppDatabasePrimaryKeySchema.define(AppDatabaseColumnSchema.define(ORDER_ID, Integer.class)),
                    AppDatabaseColumnSchema.define(USER_ID, Integer.class, true)
                )
            )
        );
    }
}

class AppDatabaseProcessStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Void input, final Communication communication) {
        System.out.println("AppDatabaseProcessStartingState.waitUntil: " + input);

        return CommandRequest.EMPTY;
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Void input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("AppDatabaseProcessStartingState.execute: " + input);

        final List<AppDatabaseRow> userTableRows = persistence.getAppDatabaseRows(SAMPLE_USER_TABLE);
        assertEquals(2, userTableRows.size());

        assertEquals(VAL_USER_ID_1_1, userTableRows.get(0).getColumnValue(USER_ID_1));
        assertEquals(VAL_USER_ID_2_1, userTableRows.get(0).getColumnValue(USER_ID_2));
        assertEquals(VAL_FIRST_NAME_1, userTableRows.get(0).getColumnValue(FIRST_NAME));
        assertEquals(VAL_LAST_NAME_1, userTableRows.get(0).getColumnValue(LAST_NAME));
        assertThrows(AppDatabaseNotFoundException.class, () -> userTableRows.get(0).getColumnValue(CREATE_TIMESTAMP));

        assertEquals(VAL_USER_ID_1_2, userTableRows.get(1).getColumnValue(USER_ID_1));
        assertEquals(VAL_USER_ID_2_2, userTableRows.get(1).getColumnValue(USER_ID_2));
        assertEquals(VAL_FIRST_NAME_2, userTableRows.get(1).getColumnValue(FIRST_NAME));
        assertEquals(VAL_LAST_NAME_2, userTableRows.get(1).getColumnValue(LAST_NAME));
        assertThrows(AppDatabaseNotFoundException.class, () -> userTableRows.get(1).getColumnValue(CREATE_TIMESTAMP));

        final List<AppDatabaseRow> orderTableRows = persistence.getAppDatabaseRows(SAMPLE_ORDER_TABLE);
        assertEquals(1, orderTableRows.size());

        assertEquals(VAL_ORDER_ID, orderTableRows.get(0).getColumnValue(ORDER_ID));
        assertEquals(VAL_USER_ID, orderTableRows.get(0).getColumnValue(USER_ID));

        // upsert
        userTableRows.get(0).upsertColumn(CREATE_TIMESTAMP, VAL_CREATE_TIMESTAMP_1);
        userTableRows.get(1).upsertColumn(CREATE_TIMESTAMP, VAL_CREATE_TIMESTAMP_2);
        userTableRows.get(1).upsertColumn(LAST_NAME, VAL_LAST_NAME_2_2);
        orderTableRows.get(0).upsertColumn(USER_ID, VAL_USER_ID_2);

        return StateDecision.singleNextState(AppDatabaseProcessNextState1.class, null);
    }
}

class AppDatabaseProcessNextState1 implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions
            .builder(AppDatabaseProcessNextState1.class)
            .appDatabaseReadRequest(
                AppDatabaseReadRequest.create(
                    // Primary key columns will always be loaded.
                    AppDatabaseTableReadRequest.create(
                        SAMPLE_USER_TABLE,
                        DatabaseLockingType.NO_LOCKING,
                        LAST_NAME,
                        CREATE_TIMESTAMP
                    ),
                    AppDatabaseTableReadRequest.create(SAMPLE_ORDER_TABLE, DatabaseLockingType.NO_LOCKING, USER_ID)
                )
            )
            .build();
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Void input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("AppDatabaseProcessNextState1.execute: " + input);

        final List<AppDatabaseRow> userTableRows = persistence.getAppDatabaseRows(SAMPLE_USER_TABLE);
        assertEquals(2, userTableRows.size());

        assertEquals(VAL_USER_ID_1_1, userTableRows.get(0).getColumnValue(USER_ID_1));
        assertEquals(VAL_USER_ID_2_1, userTableRows.get(0).getColumnValue(USER_ID_2));
        assertThrows(AppDatabaseNotFoundException.class, () -> userTableRows.get(0).getColumnValue(FIRST_NAME));
        assertEquals(VAL_LAST_NAME_1, userTableRows.get(0).getColumnValue(LAST_NAME));
        assertEquals(VAL_CREATE_TIMESTAMP_1, userTableRows.get(0).getColumnValue(CREATE_TIMESTAMP));

        assertEquals(VAL_USER_ID_1_2, userTableRows.get(1).getColumnValue(USER_ID_1));
        assertEquals(VAL_USER_ID_2_2, userTableRows.get(1).getColumnValue(USER_ID_2));
        assertThrows(AppDatabaseNotFoundException.class, () -> userTableRows.get(1).getColumnValue(FIRST_NAME));
        assertEquals(VAL_LAST_NAME_2_2, userTableRows.get(1).getColumnValue(LAST_NAME));
        assertEquals(VAL_CREATE_TIMESTAMP_2, userTableRows.get(1).getColumnValue(CREATE_TIMESTAMP));

        final List<AppDatabaseRow> orderTableRows = persistence.getAppDatabaseRows(SAMPLE_ORDER_TABLE);
        assertEquals(1, orderTableRows.size());

        assertEquals(VAL_ORDER_ID, orderTableRows.get(0).getColumnValue(ORDER_ID));
        assertEquals(VAL_USER_ID_2, orderTableRows.get(0).getColumnValue(USER_ID));

        return StateDecision.singleNextState(AppDatabaseProcessNextState2.class, null);
    }
}

class AppDatabaseProcessNextState2 implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions
            .builder(AppDatabaseProcessNextState2.class)
            .appDatabaseReadRequest(
                AppDatabaseReadRequest.create(
                    AppDatabaseTableReadRequest.create(SAMPLE_ORDER_TABLE, DatabaseLockingType.NO_LOCKING)
                )
            )
            .build();
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Void input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("AppDatabaseProcessNextState2.execute: " + input);

        assertThrows(AppDatabaseNotFoundException.class, () -> persistence.getAppDatabaseRows(SAMPLE_USER_TABLE));

        final List<AppDatabaseRow> orderTableRows = persistence.getAppDatabaseRows(SAMPLE_ORDER_TABLE);
        assertEquals(1, orderTableRows.size());

        assertEquals(VAL_ORDER_ID, orderTableRows.get(0).getColumnValue(ORDER_ID));
        assertThrows(AppDatabaseNotFoundException.class, () -> orderTableRows.get(0).getColumnValue(USER_ID));

        return StateDecision.forceCompleteProcess();
    }
}
