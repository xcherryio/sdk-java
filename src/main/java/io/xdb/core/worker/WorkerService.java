package io.xdb.core.worker;

import io.xdb.core.registry.Registry;
import io.xdb.core.state.AsyncState;
import io.xdb.gen.models.AsyncStateExecuteRequest;
import io.xdb.gen.models.AsyncStateExecuteResponse;
import io.xdb.gen.models.AsyncStateWaitUntilRequest;
import io.xdb.gen.models.AsyncStateWaitUntilResponse;
import io.xdb.gen.models.CommandRequest;
import io.xdb.gen.models.StateDecision;

public class WorkerService {

    public static final String API_PATH_ASYNC_STATE_WAIT_UNTIL = "/api/v1/xdb/worker/async-state/wait-until";
    public static final String API_PATH_ASYNC_STATE_EXECUTE = "/api/v1/xdb/worker/async-state/execute";

    private final Registry registry;
    private final WorkerServiceOptions workerServiceOptions;

    public WorkerService(final Registry registry, final WorkerServiceOptions workerServiceOptions) {
        this.registry = registry;
        this.workerServiceOptions = workerServiceOptions;
    }

    public AsyncStateWaitUntilResponse handleAsyncStateWaitUntil(final AsyncStateWaitUntilRequest request) {
        final AsyncState state = registry.getProcessState(request.getProcessType(), request.getStateId());
        final Object input = workerServiceOptions
            .getObjectEncoder()
            .decode(request.getStateInput(), state.getInputType());

        // TODO
        final CommandRequest commandRequest = state.waitUntil(input);

        return new AsyncStateWaitUntilResponse().commandRequest(commandRequest);
    }

    public AsyncStateExecuteResponse handleAsyncStateExecute(final AsyncStateExecuteRequest request) {
        final AsyncState state = registry.getProcessState(request.getProcessType(), request.getStateId());
        final Object input = workerServiceOptions
            .getObjectEncoder()
            .decode(request.getStateInput(), state.getInputType());

        // TODO
        final StateDecision stateDecision = state.execute(input);

        return new AsyncStateExecuteResponse().stateDecision(stateDecision);
    }
}
