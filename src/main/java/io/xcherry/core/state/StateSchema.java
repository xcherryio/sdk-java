package io.xcherry.core.state;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StateSchema {

    private final AsyncState startingState;
    private final List<AsyncState> allStates;

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
