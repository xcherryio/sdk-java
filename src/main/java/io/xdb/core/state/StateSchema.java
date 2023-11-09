package io.xdb.core.state;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class StateSchema {

    private final AsyncState startingState;
    private final List<AsyncState> allStates;

    private StateSchema(final AsyncState startingState, final List<AsyncState> allStates) {
        this.startingState = startingState;
        this.allStates = allStates;
    }

    public static StateSchema withStartingState(final AsyncState startingState, final AsyncState... nonStartingStates) {
        final List<AsyncState> states = Arrays.stream(nonStartingStates).collect(Collectors.toList());
        if (startingState != null) {
            states.add(startingState);
        }

        return new StateSchema(startingState, states);
    }

    public static StateSchema noStartingState(final AsyncState... nonStartingStates) {
        return withStartingState(null, nonStartingStates);
    }
}
