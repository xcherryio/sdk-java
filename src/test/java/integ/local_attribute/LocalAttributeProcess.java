package integ.local_attribute;

import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_KEY_1;
import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_KEY_2;
import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_KEY_3;
import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_NOT_EXIST;
import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_VALUE_1;
import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_VALUE_1_2;
import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_VALUE_2;
import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_VALUE_2_2;
import static integ.local_attribute.TestLocalAttributeProcess.LOCAL_ATTRIBUTE_VALUE_3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.xcherry.core.command.CommandRequest;
import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.persistence.Persistence;
import io.xcherry.core.persistence.readrequest.LocalAttributeReadRequest;
import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.core.persistence.schema.localattribute.LocalAttributeKeySchema;
import io.xcherry.core.persistence.schema.localattribute.LocalAttributeSchema;
import io.xcherry.core.process.Process;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.AsyncStateOptions;
import io.xcherry.core.state.StateDecision;
import io.xcherry.core.state.StateSchema;
import io.xcherry.gen.models.LockType;
import org.springframework.stereotype.Component;

@Component
public class LocalAttributeProcess implements Process {

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(
            new LocalAttributeProcessStartingState(),
            new LocalAttributeProcessNextState1(),
            new LocalAttributeProcessNextState2()
        );
    }

    @Override
    public PersistenceSchema getPersistenceSchema() {
        return PersistenceSchema.define(
            LocalAttributeSchema.define(
                LocalAttributeKeySchema.define(LOCAL_ATTRIBUTE_KEY_1, String.class),
                LocalAttributeKeySchema.define(LOCAL_ATTRIBUTE_KEY_2, Integer.class, false, false),
                LocalAttributeKeySchema.define(LOCAL_ATTRIBUTE_KEY_3, String.class, false, false)
            )
        );
    }
}

class LocalAttributeProcessStartingState implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Void input, final Communication communication) {
        System.out.println("LocalAttributeProcessStartingState.waitUntil: " + input);

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
        System.out.println("LocalAttributeProcessStartingState.execute: " + input);

        assertEquals(LOCAL_ATTRIBUTE_VALUE_1, persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_1));
        assertNull(persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_2));
        assertNull(persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_3));
        assertNull(persistence.getLocalAttribute(LOCAL_ATTRIBUTE_NOT_EXIST));

        // upsert
        persistence.setLocalAttribute(LOCAL_ATTRIBUTE_KEY_3, LOCAL_ATTRIBUTE_VALUE_3);

        return StateDecision.singleNextState(LocalAttributeProcessNextState1.class, null);
    }
}

class LocalAttributeProcessNextState1 implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions
            .builder(LocalAttributeProcessNextState1.class)
            .localAttributeReadRequest(
                LocalAttributeReadRequest.create(LockType.NO_LOCKING, LOCAL_ATTRIBUTE_KEY_2, LOCAL_ATTRIBUTE_KEY_3)
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
        System.out.println("LocalAttributeProcessNextState1.execute: " + input);

        assertNull(persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_1));
        assertEquals(LOCAL_ATTRIBUTE_VALUE_2, persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_2));
        assertEquals(LOCAL_ATTRIBUTE_VALUE_3, persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_3));
        assertNull(persistence.getLocalAttribute(LOCAL_ATTRIBUTE_NOT_EXIST));

        // upsert
        persistence.setLocalAttribute(LOCAL_ATTRIBUTE_KEY_1, LOCAL_ATTRIBUTE_VALUE_1_2);
        persistence.setLocalAttribute(LOCAL_ATTRIBUTE_KEY_2, LOCAL_ATTRIBUTE_VALUE_2_2);

        return StateDecision.singleNextState(LocalAttributeProcessNextState2.class, null);
    }
}

class LocalAttributeProcessNextState2 implements AsyncState<Void> {

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions
            .builder(LocalAttributeProcessNextState2.class)
            .localAttributeReadRequest(
                LocalAttributeReadRequest.create(
                    LockType.NO_LOCKING,
                    LOCAL_ATTRIBUTE_KEY_1,
                    LOCAL_ATTRIBUTE_KEY_2,
                    LOCAL_ATTRIBUTE_KEY_3
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
        System.out.println("LocalAttributeProcessNextState2.execute: " + input);

        assertEquals(LOCAL_ATTRIBUTE_VALUE_1_2, persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_1));
        assertEquals(LOCAL_ATTRIBUTE_VALUE_2_2, persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_2));
        assertEquals(LOCAL_ATTRIBUTE_VALUE_3, persistence.getLocalAttribute(LOCAL_ATTRIBUTE_KEY_3));
        assertNull(persistence.getLocalAttribute(LOCAL_ATTRIBUTE_NOT_EXIST));

        return StateDecision.forceCompleteProcess();
    }
}
