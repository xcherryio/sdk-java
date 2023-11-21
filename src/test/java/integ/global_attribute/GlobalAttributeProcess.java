package integ.global_attribute;

import static integ.global_attribute.TestGlobalAttributeProcess.COL_KEY_1;
import static integ.global_attribute.TestGlobalAttributeProcess.COL_KEY_2;
import static integ.global_attribute.TestGlobalAttributeProcess.COL_KEY_3;
import static integ.global_attribute.TestGlobalAttributeProcess.COL_VALUE_1;
import static integ.global_attribute.TestGlobalAttributeProcess.COL_VALUE_1_2;
import static integ.global_attribute.TestGlobalAttributeProcess.COL_VALUE_2;
import static integ.global_attribute.TestGlobalAttributeProcess.COL_VALUE_3;
import static integ.global_attribute.TestGlobalAttributeProcess.PK_KEY;
import static integ.global_attribute.TestGlobalAttributeProcess.PK_VALUE;
import static integ.global_attribute.TestGlobalAttributeProcess.TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.xcherry.core.command.CommandRequest;
import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.exception.persistence.GlobalAttributeNotFoundException;
import io.xcherry.core.persistence.Persistence;
import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.core.persistence.schema.PersistenceTableColumnSchema;
import io.xcherry.core.persistence.schema.PersistenceTableSchema;
import io.xcherry.core.persistence.schema_to_load.PersistenceSchemaToLoadData;
import io.xcherry.core.persistence.schema_to_load.PersistenceTableSchemaToLoadData;
import io.xcherry.core.process.Process;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.AsyncStateOptions;
import io.xcherry.core.state.StateDecision;
import io.xcherry.core.state.StateSchema;
import org.springframework.stereotype.Component;

@Component
public class GlobalAttributeProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(
            new GlobalAttributeProcessStartingState(),
            new GlobalAttributeProcessNextState1(),
            new GlobalAttributeProcessNextState2()
        );
    }

    @Override
    public PersistenceSchema getPersistenceSchema() {
        return PersistenceSchema.withGlobalAttributes(
            PersistenceTableSchema.create(
                TABLE_NAME,
                PersistenceTableColumnSchema.create(PK_KEY, String.class, true, true),
                PersistenceTableColumnSchema.create(COL_KEY_1, String.class, false, true),
                PersistenceTableColumnSchema.create(COL_KEY_2, String.class, false, true),
                PersistenceTableColumnSchema.create(COL_KEY_3, Integer.class, false, false)
            )
        );
    }
}

class GlobalAttributeProcessStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
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
        assertThrows(
            GlobalAttributeNotFoundException.class,
            () -> persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_3)
        );

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
            .persistenceSchemaToLoadData(
                PersistenceSchemaToLoadData.withGlobalAttributes(
                    PersistenceTableSchemaToLoadData.create(TABLE_NAME, COL_KEY_1, COL_KEY_3)
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

        return StateDecision.singleNextState(GlobalAttributeProcessNextState2.class, null);
    }
}

class GlobalAttributeProcessNextState2 implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions
            .builder(GlobalAttributeProcessNextState2.class)
            .persistenceSchemaToLoadData(PersistenceSchemaToLoadData.EMPTY())
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
        System.out.println("GlobalAttributeProcessNextState2.execute: " + input);

        assertThrows(GlobalAttributeNotFoundException.class, () -> persistence.getGlobalAttribute(TABLE_NAME, PK_KEY));
        assertThrows(
            GlobalAttributeNotFoundException.class,
            () -> persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_1)
        );
        assertThrows(
            GlobalAttributeNotFoundException.class,
            () -> persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_2)
        );
        assertThrows(
            GlobalAttributeNotFoundException.class,
            () -> persistence.getGlobalAttribute(TABLE_NAME, COL_KEY_3)
        );

        return StateDecision.forceCompleteProcess();
    }
}
