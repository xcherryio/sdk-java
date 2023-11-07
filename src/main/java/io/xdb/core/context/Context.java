package io.xdb.core.context;

import io.xdb.gen.models.StateApiType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Context {

    private final String processId;
    private final String processExecutionId;
    private final long processStartedTimestamp;
    private final String stateExecutionId;
    private final long firstAttemptTimestamp;
    private final int attempt;
    private final String recoverFromStateExecutionId;
    private final StateApiType recoverFromApi;

    public static Context fromApiModel(final io.xdb.gen.models.Context context) {
        return Context
            .builder()
            .processId(context.getProcessId())
            .processExecutionId(context.getProcessExecutionId())
            .processStartedTimestamp(context.getProcessStartedTimestamp())
            .stateExecutionId(context.getStateExecutionId())
            .firstAttemptTimestamp(context.getFirstAttemptTimestamp())
            .attempt(context.getAttempt())
            .recoverFromStateExecutionId(context.getRecoverFromStateExecutionId())
            .recoverFromApi(context.getRecoverFromApi())
            .build();
    }
}
