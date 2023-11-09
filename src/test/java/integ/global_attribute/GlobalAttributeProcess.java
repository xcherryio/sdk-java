package integ.global_attribute;

import static integ.global_attribute.GlobalAttributeProcess.COL_KEY_1;
import static integ.global_attribute.GlobalAttributeProcess.COL_KEY_2;
import static integ.global_attribute.GlobalAttributeProcess.COL_KEY_3;
import static integ.global_attribute.GlobalAttributeProcess.COL_VALUE_1;
import static integ.global_attribute.GlobalAttributeProcess.COL_VALUE_1_2;
import static integ.global_attribute.GlobalAttributeProcess.COL_VALUE_2;
import static integ.global_attribute.GlobalAttributeProcess.COL_VALUE_3;
import static integ.global_attribute.GlobalAttributeProcess.PK_KEY;
import static integ.global_attribute.GlobalAttributeProcess.PK_VALUE;
import static integ.global_attribute.GlobalAttributeProcess.TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableList;
import io.xdb.core.command.CommandRequest;
import io.xdb.core.command.CommandResults;
import io.xdb.core.communication.Communication;
import io.xdb.core.context.Context;
import io.xdb.core.exception.GlobalAttributeNotFoundException;
import io.xdb.core.persistence.Persistence;
import io.xdb.core.process.Process;
import io.xdb.core.process.ProcessOptions;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.AsyncStateOptions;
import io.xdb.core.state.StateDecision;
import io.xdb.core.state.StateSchema;
import io.xdb.gen.models.AttributeWriteConflictMode;
import io.xdb.gen.models.GlobalAttributeConfig;
import io.xdb.gen.models.GlobalAttributeTableConfig;
import io.xdb.gen.models.LoadGlobalAttributesRequest;
import io.xdb.gen.models.ProcessStartConfig;
import io.xdb.gen.models.TableColumnDef;
import io.xdb.gen.models.TableColumnValue;
import io.xdb.gen.models.TableReadLockingPolicy;
import io.xdb.gen.models.TableReadRequest;
import org.springframework.stereotype.Component;

@Component
public class GlobalAttributeProcess implements Process {

    public static final String TABLE_NAME = "sample_user_table";
    public static final String PK_KEY = "user_id";
    public static final String PK_VALUE = "pk_value";
    public static final String COL_KEY_1 = "first_name";
    public static final String COL_VALUE_1 = "col_1_value";
    public static final String COL_VALUE_1_2 = "col_1_value_2";
    public static final String COL_KEY_2 = "last_name";
    public static final String COL_VALUE_2 = "col_2_value";
    public static final String COL_KEY_3 = "create_timestamp";
    public static final String COL_VALUE_3 = "111";

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(
            new GlobalAttributeProcessStartingState(),
            new GlobalAttributeProcessNextState1()
        );
    }

    @Override
    public ProcessOptions getOptions() {
        return ProcessOptions
            .builder(GlobalAttributeProcess.class)
            .processStartConfig(
                new ProcessStartConfig()
                    .globalAttributeConfig(
                        new GlobalAttributeConfig()
                            .tableConfigs(
                                ImmutableList.of(
                                    new GlobalAttributeTableConfig()
                                        .tableName(TABLE_NAME)
                                        .primaryKey(new TableColumnValue().dbColumn(PK_KEY).dbQueryValue(PK_VALUE))
                                        .initialWrite(
                                            ImmutableList.of(
                                                new TableColumnValue().dbColumn(COL_KEY_1).dbQueryValue(COL_VALUE_1),
                                                new TableColumnValue().dbColumn(COL_KEY_2).dbQueryValue(COL_VALUE_2)
                                            )
                                        )
                                        .initialWriteMode(AttributeWriteConflictMode.RETURNERRORONCONFLICT)
                                )
                            )
                    )
            )
            .build();
    }
}

class GlobalAttributeProcessStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions
            .builder(GlobalAttributeProcessStartingState.class)
            .loadGlobalAttributesRequest(
                new LoadGlobalAttributesRequest()
                    .tableRequests(
                        ImmutableList.of(
                            new TableReadRequest()
                                .tableName(TABLE_NAME)
                                .lockingPolicy(TableReadLockingPolicy.NO_LOCKING)
                                .columns(
                                    ImmutableList.of(
                                        new TableColumnDef().dbColumn(PK_KEY),
                                        new TableColumnDef().dbColumn(COL_KEY_1),
                                        new TableColumnDef().dbColumn(COL_KEY_2)
                                    )
                                )
                        )
                    )
            )
            .build();
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Void input, final Communication communication) {
        System.out.println("GlobalAttributeProcessStartingState.waitUntil: " + input);

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
        System.out.println("GlobalAttributeProcessStartingState.execute: " + input);

        assertEquals(PK_VALUE, persistence.getGlobalAttribute(TABLE_NAME, PK_KEY));
        assertEquals(COL_VALUE_1, persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_1));
        assertEquals(COL_VALUE_2, persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_2));

        persistence.upsertGlobalAttribute(TABLE_NAME, COL_KEY_1, COL_VALUE_1_2);
        persistence.upsertGlobalAttribute(TABLE_NAME, COL_KEY_3, COL_VALUE_3);

        return StateDecision.singleNextState(GlobalAttributeProcessNextState1.class, null);
    }
}

class GlobalAttributeProcessNextState1 implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions
            .builder(GlobalAttributeProcessNextState1.class)
            .loadGlobalAttributesRequest(
                new LoadGlobalAttributesRequest()
                    .tableRequests(
                        ImmutableList.of(
                            new TableReadRequest()
                                .tableName(TABLE_NAME)
                                .lockingPolicy(TableReadLockingPolicy.NO_LOCKING)
                                .columns(
                                    ImmutableList.of(
                                        new TableColumnDef().dbColumn(COL_KEY_1),
                                        new TableColumnDef().dbColumn(COL_KEY_3)
                                    )
                                )
                        )
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
        System.out.println("GlobalAttributeProcessNextState1.execute: " + input);

        assertThrows(GlobalAttributeNotFoundException.class, () -> persistence.getGlobalAttribute(TABLE_NAME, PK_KEY));
        assertEquals(COL_VALUE_1_2, persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_1));
        assertThrows(
            GlobalAttributeNotFoundException.class,
            () -> persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_2)
        );
        assertEquals(COL_VALUE_3, persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_3));

        return StateDecision.forceCompleteProcess();
    }
}
