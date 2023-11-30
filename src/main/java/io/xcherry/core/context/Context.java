package io.xcherry.core.context;

import io.xcherry.gen.models.WorkerApiType;
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
    private final WorkerApiType recoverFromApi;

    public static Context fromApiModel(final io.xcherry.gen.models.Context context) {
        return Context
            .builder()
            .processId(context.getProcessId())
            .processExecutionId(context.getProcessExecutionId())
            .processStartedTimestamp(context.getProcessStartedTimestamp())
            .stateExecutionId(context.getStateExecutionId())
            .firstAttemptTimestamp(context.getFirstAttemptTimestamp() == null ? 0 : context.getFirstAttemptTimestamp())
            .attempt(context.getAttempt() == null ? 0 : context.getAttempt())
            .recoverFromStateExecutionId(context.getRecoverFromStateExecutionId())
            .recoverFromApi(context.getRecoverFromApi())
            .build();
    }
}
