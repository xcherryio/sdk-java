package integ.basic;

import static integ.basic.BasicProcess.INPUT;
import static integ.basic.BasicProcess.STATE_ID_NEXT_1;
import static integ.basic.BasicProcess.STATE_ID_NEXT_2;

import io.xcherry.core.command.CommandRequest;
import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.persistence.Persistence;
import io.xcherry.core.process.Process;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.AsyncStateOptions;
import io.xcherry.core.state.StateDecision;
import io.xcherry.core.state.StateMovement;
import io.xcherry.core.state.StateSchema;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

@Component
public class BasicProcess implements Process {

    public static final String STATE_ID_NEXT_1 = "STATE_ID_NEXT_1";
    public static final String STATE_ID_NEXT_2 = "STATE_ID_NEXT_2";
    public static final Integer INPUT = 11;

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new BasicStartingState(), new NextState1(), new NextState2());
    }
}

class BasicStartingState implements AsyncState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Integer input, final Communication communication) {
        System.out.println("BasicStartingState.waitUntil: " + input);
        Assertions.assertEquals(INPUT, input);

        return CommandRequest.EMPTY;
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Integer input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("BasicStartingState.execute: " + input);
        Assertions.assertEquals(INPUT, input);

        return StateDecision.multipleNextStates(
            StateMovement.builder().stateId(STATE_ID_NEXT_1).stateInput(input + 1).build(),
            StateMovement.builder().stateId(STATE_ID_NEXT_2).stateInput(input + 2).build()
        );
    }
}

class NextState1 implements AsyncState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions.builder(this.getClass()).id(STATE_ID_NEXT_1).build();
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Integer input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("NextState1.execute: " + input);
        Assertions.assertEquals(INPUT + 1, input);

        return StateDecision.deadEnd();
    }
}

class NextState2 implements AsyncState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions.builder(this.getClass()).id(STATE_ID_NEXT_2).build();
    }

    @Override
    public CommandRequest waitUntil(final Context context, final Integer input, final Communication communication) {
        System.out.println("NextState2.waitUntil: " + input);
        Assertions.assertEquals(INPUT + 2, input);

        return CommandRequest.EMPTY;
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Integer input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("NextState2.execute: " + input);
        Assertions.assertEquals(INPUT + 2, input);

        return StateDecision.deadEnd();
    }
}
