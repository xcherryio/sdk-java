package integ.rpc;

import static integ.rpc.RpcProcess.INPUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.persistence.Persistence;
import io.xcherry.core.process.Process;
import io.xcherry.core.rpc.RPC;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.state.StateDecision;
import io.xcherry.core.state.StateSchema;
import org.springframework.stereotype.Component;

@Component
public class RpcProcess implements Process {

    public static final Integer INPUT = 11;

    @Override
    public StateSchema getStateSchema() {
        return StateSchema.withStartingState(new RpcStartingState(), new RpcNextState1());
    }

    @RPC
    public String triggerNextState(
        final Context context,
        final int input,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("triggerNextState: " + input);

        assertEquals(INPUT, input);

        communication.triggerSingleStateMovement(RpcNextState1.class, input);

        return String.valueOf(input + 1);
    }

    @RPC
    public void triggerNextStateNoOutput(
        final Context context,
        final int input,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("triggerNextStateNoOutput: " + input);

        assertEquals(INPUT, input);

        communication.triggerSingleStateMovement(RpcNextState1.class, input);
    }

    @RPC
    public String triggerNextStateNoInput(
        final Context context,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("triggerNextStateNoInput");

        communication.triggerSingleStateMovement(RpcNextState1.class, INPUT);

        return String.valueOf(INPUT + 2);
    }

    @RPC
    public void triggerNextStateNoInputNoOutput(
        final Context context,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("triggerNextStateNoInputNoOutput");

        communication.triggerSingleStateMovement("RpcNextState1", INPUT);
    }
}

class RpcStartingState implements AsyncState<Void> {

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
        System.out.println("RpcStartingState.execute: " + input);

        return StateDecision.deadEnd();
    }
}

class RpcNextState1 implements AsyncState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public StateDecision execute(
        final Context context,
        final Integer input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    ) {
        System.out.println("RpcNextState1.execute: " + input);

        assertEquals(INPUT, input);

        return StateDecision.gracefulCompleteProcess();
    }
}
