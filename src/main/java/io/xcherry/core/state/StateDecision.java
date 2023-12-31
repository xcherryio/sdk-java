package io.xcherry.core.state;

import com.google.common.collect.ImmutableList;
import io.xcherry.core.utils.ProcessUtil;
import io.xcherry.gen.models.ThreadCloseDecision;
import io.xcherry.gen.models.ThreadCloseType;
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
    private final io.xcherry.gen.models.StateDecision stateDecision;
    private final List<StateMovement> nextStates;

    public static StateDecision deadEnd() {
        return StateDecision
            .builder()
            .stateDecision(
                new io.xcherry.gen.models.StateDecision()
                    .threadCloseDecision(new ThreadCloseDecision().closeType(ThreadCloseType.DEAD_END))
            )
            .build();
    }

    public static StateDecision gracefulCompleteProcess() {
        return StateDecision
            .builder()
            .stateDecision(
                new io.xcherry.gen.models.StateDecision()
                    .threadCloseDecision(new ThreadCloseDecision().closeType(ThreadCloseType.GRACEFUL_COMPLETE_PROCESS))
            )
            .build();
    }

    public static StateDecision forceCompleteProcess() {
        return StateDecision
            .builder()
            .stateDecision(
                new io.xcherry.gen.models.StateDecision()
                    .threadCloseDecision(new ThreadCloseDecision().closeType(ThreadCloseType.FORCE_COMPLETE_PROCESS))
            )
            .build();
    }

    public static StateDecision forceFailProcess() {
        return StateDecision
            .builder()
            .stateDecision(
                new io.xcherry.gen.models.StateDecision()
                    .threadCloseDecision(new ThreadCloseDecision().closeType(ThreadCloseType.FORCE_FAIL_PROCESS))
            )
            .build();
    }

    // TODO: option override
    public static StateDecision singleNextState(final Class<? extends AsyncState> stateClass, final Object stateInput) {
        return singleNextState(ProcessUtil.getClassSimpleName(stateClass), stateInput);
    }

    public static StateDecision singleNextState(final String stateId, final Object stateInput) {
        final StateMovement stateMovement = StateMovement.builder().stateId(stateId).stateInput(stateInput).build();
        return StateDecision.builder().nextStates(ImmutableList.of(stateMovement)).build();
    }

    public static StateDecision multipleNextStates(final Class<? extends AsyncState>... stateClasses) {
        return multipleNextStates(
            Arrays.stream(stateClasses).map(ProcessUtil::getClassSimpleName).toArray(String[]::new)
        );
    }

    public static StateDecision multipleNextStates(final String... stateIds) {
        final ArrayList<StateMovement> stateMovements = new ArrayList<>();
        for (final String stateId : stateIds) {
            stateMovements.add(StateMovement.builder().stateId(stateId).build());
        }
        return multipleNextStates(stateMovements);
    }

    public static StateDecision multipleNextStates(final StateMovement... stateMovements) {
        return multipleNextStates(Arrays.stream(stateMovements).collect(Collectors.toList()));
    }

    public static StateDecision multipleNextStates(final List<StateMovement> stateMovements) {
        return StateDecision.builder().nextStates(stateMovements).build();
    }
}
