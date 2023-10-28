package io.xdb.core.state.feature;

import io.xdb.core.communication.Communication;
import io.xdb.gen.models.CommandResults;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AsyncStateExecuteFeatures {

    final Communication communication;
    final CommandResults commandResults;
}
