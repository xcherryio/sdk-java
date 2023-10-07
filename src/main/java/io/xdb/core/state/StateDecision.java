package io.xdb.core.state;

import com.google.common.collect.ImmutableList;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.ThreadCloseDecision;
import io.xdb.gen.models.ThreadCloseType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StateDecision {

    // directly return stateDecision if it presents
    private final io.xdb.gen.models.StateDecision stateDecision;
    private final List<StateMovement> nextStates;

    public static StateDecision deadEnd() {
        return StateDecision
            .builder()
            .stateDecision(
                new io.xdb.gen.models.StateDecision()
                    .threadCloseDecision(new ThreadCloseDecision().closeType(ThreadCloseType.DEAD_END))
            )
            .build();
    }

    public static StateDecision gracefulCompleteProcess() {
        return StateDecision
            .builder()
            .stateDecision(
                new io.xdb.gen.models.StateDecision()
                    .threadCloseDecision(new ThreadCloseDecision().closeType(ThreadCloseType.GRACEFUL_COMPLETE_PROCESS))
            )
            .build();
    }

    public static StateDecision forceCompleteProcess() {
        return StateDecision
            .builder()
            .stateDecision(
                new io.xdb.gen.models.StateDecision()
                    .threadCloseDecision(new ThreadCloseDecision().closeType(ThreadCloseType.FORCE_COMPLETE_PROCESS))
            )
            .build();
    }

    public static StateDecision forceFailProcess() {
        return StateDecision
            .builder()
            .stateDecision(
                new io.xdb.gen.models.StateDecision()
                    .threadCloseDecision(new ThreadCloseDecision().closeType(ThreadCloseType.FORCE_FAIL_PROCESS))
            )
            .build();
    }

    // TODO: option override
    public static StateDecision singleNextState(final Class<? extends AsyncState> stateClass, final Object stateInput) {
        final StateMovement stateMovement = StateMovement
            .builder()
            .stateId(ProcessUtil.getStateId(stateClass))
            .stateInput(stateInput)
            .build();
        return StateDecision.builder().nextStates(ImmutableList.of(stateMovement)).build();
    }

    public static StateDecision singleNextState(final String stateId, final Object stateInput) {
        final StateMovement stateMovement = StateMovement.builder().stateId(stateId).stateInput(stateInput).build();
        return StateDecision.builder().nextStates(ImmutableList.of(stateMovement)).build();
    }

    public static StateDecision multipleNextStates(final Class<? extends AsyncState>... stateClasses) {
        final ArrayList<StateMovement> stateMovements = new ArrayList<>();
        for (final Class<? extends AsyncState> stateClass : stateClasses) {
            stateMovements.add(StateMovement.builder().stateId(ProcessUtil.getStateId(stateClass)).build());
        }
        return StateDecision.builder().nextStates(stateMovements).build();
    }

    public static StateDecision multipleNextStates(final String... stateIds) {
        final ArrayList<StateMovement> stateMovements = new ArrayList<>();
        for (final String stateId : stateIds) {
            stateMovements.add(StateMovement.builder().stateId(stateId).build());
        }
        return StateDecision.builder().nextStates(stateMovements).build();
    }

    public static StateDecision multipleNextStates(final StateMovement... stateMovements) {
        return StateDecision.builder().nextStates(Arrays.stream(stateMovements).collect(Collectors.toList())).build();
    }
}
