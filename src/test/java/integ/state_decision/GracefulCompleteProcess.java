package integ.state_decision;

import static integ.state_decision.GracefulCompleteProcess.INPUT;
import static integ.state_decision.GracefulCompleteProcess.STATE_ID_NEXT_1;
import static integ.state_decision.GracefulCompleteProcess.STATE_ID_NEXT_2;

import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.AsyncStateOptions;
import io.xdb.core.state.StateDecision;
import io.xdb.core.state.StateMovement;
import io.xdb.core.state.StateSchema;
import io.xdb.gen.models.CommandRequest;
import io.xdb.gen.models.CommandWaitingType;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

@Component
public class GracefulCompleteProcess implements Process {

    public static final String STATE_ID_NEXT_1 = "STATE_ID_NEXT_1";
    public static final String STATE_ID_NEXT_2 = "STATE_ID_NEXT_2";
    public static final Integer INPUT = 11;

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new GCStartingState(), new GCNextState1(), new GCNextState2());
    }
}

class GCStartingState implements AsyncState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(final Integer input) {
        System.out.println("GCStartingState.waitUntil: " + input);
        Assertions.assertEquals(INPUT, input);

        return new CommandRequest().waitingType(CommandWaitingType.EMPTYCOMMAND);
    }

    @Override
    public StateDecision execute(final Integer input) {
        System.out.println("GCStartingState.execute: " + input);
        Assertions.assertEquals(INPUT, input);

        return StateDecision.multipleNextStates(
            StateMovement.builder().stateId(STATE_ID_NEXT_1).stateInput(input + 1).build(),
            StateMovement.builder().stateId(STATE_ID_NEXT_2).stateInput(input + 2).build()
        );
    }
}

class GCNextState1 implements AsyncState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions.builder(this.getClass()).id(STATE_ID_NEXT_1).build();
    }

    @Override
    public StateDecision execute(final Integer input) {
        System.out.println("GCNextState1.execute: " + input);
        Assertions.assertEquals(INPUT + 1, input);

        return StateDecision.gracefulCompleteProcess();
    }
}

class GCNextState2 implements AsyncState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public AsyncStateOptions getOptions() {
        return AsyncStateOptions.builder(this.getClass()).id(STATE_ID_NEXT_2).build();
    }

    @Override
    public CommandRequest waitUntil(final Integer input) {
        System.out.println("GCNextState2.waitUntil: " + input);
        Assertions.assertEquals(INPUT + 2, input);

        return new CommandRequest().waitingType(CommandWaitingType.EMPTYCOMMAND);
    }

    @Override
    public StateDecision execute(final Integer input) {
        System.out.println("GCNextState2.execute: " + input);
        Assertions.assertEquals(INPUT + 2, input);

        return StateDecision.deadEnd();
    }
}
