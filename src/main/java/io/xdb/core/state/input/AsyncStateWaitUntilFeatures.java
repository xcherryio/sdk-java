package io.xdb.core.state.input;

import io.xdb.core.communication.Communication;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AsyncStateWaitUntilFeatures {

    final Communication communication;
}
