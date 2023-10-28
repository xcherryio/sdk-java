package io.xdb.core.state.feature;

import io.xdb.core.communication.Communication;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AsyncStateWaitUntilFeatures {

    final Communication communication;
}
