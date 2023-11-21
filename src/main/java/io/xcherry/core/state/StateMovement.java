package io.xcherry.core.state;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StateMovement {

    private final String stateId;
    private final Object stateInput;
}
