package io.xdb.core.state;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StateSchema {

    private final AsyncState startingState;
    private final List<AsyncState> allStates;

    public static StateSchema withStartingState(final AsyncState startingState, final AsyncState... nonStartingStates) {
        final List<AsyncState> states = Arrays.stream(nonStartingStates).collect(Collectors.toList());
        if (startingState != null) {
            states.add(startingState);
        }

        final StateSchemaBuilder stateSchemaBuilder = StateSchema.builder().allStates(states);

        if (startingState != null) {
            stateSchemaBuilder.startingState(startingState);
        }

        return stateSchemaBuilder.build();
    }

    public static StateSchema noStartingState(final AsyncState... nonStartingStates) {
        return withStartingState(null, nonStartingStates);
    }
}
