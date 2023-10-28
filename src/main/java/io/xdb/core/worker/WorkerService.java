package io.xdb.core.worker;

import io.xdb.core.communication.Communication;
import io.xdb.core.registry.Registry;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.feature.AsyncStateExecuteFeatures;
import io.xdb.core.state.feature.AsyncStateWaitUntilFeatures;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.AsyncStateExecuteRequest;
import io.xdb.gen.models.AsyncStateExecuteResponse;
import io.xdb.gen.models.AsyncStateWaitUntilRequest;
import io.xdb.gen.models.AsyncStateWaitUntilResponse;
import io.xdb.gen.models.CommandRequest;
import io.xdb.gen.models.StateDecision;
import io.xdb.gen.models.StateMovement;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkerService {

    public static final String API_PATH_ASYNC_STATE_WAIT_UNTIL = "/api/v1/xdb/worker/async-state/wait-until";
    public static final String API_PATH_ASYNC_STATE_EXECUTE = "/api/v1/xdb/worker/async-state/execute";

    private final Registry registry;
    private final WorkerServiceOptions workerServiceOptions;

    public AsyncStateWaitUntilResponse handleAsyncStateWaitUntil(final AsyncStateWaitUntilRequest request) {
        final AsyncState state = registry.getProcessState(request.getProcessType(), request.getStateId());
        final Object input = workerServiceOptions
            .getObjectEncoder()
            .decode(request.getStateInput(), state.getInputType());

        final Communication communication = new Communication(workerServiceOptions.getObjectEncoder());

        final CommandRequest commandRequest = state.waitUntil(input, new AsyncStateWaitUntilFeatures(communication));

        return new AsyncStateWaitUntilResponse()
            .commandRequest(commandRequest)
            .publishToLocalQueue(communication.getLocalQueueMessagesToPublish());
    }

    public AsyncStateExecuteResponse handleAsyncStateExecute(final AsyncStateExecuteRequest request) {
        final AsyncState state = registry.getProcessState(request.getProcessType(), request.getStateId());
        final Object input = workerServiceOptions
            .getObjectEncoder()
            .decode(request.getStateInput(), state.getInputType());

        final Communication communication = new Communication(workerServiceOptions.getObjectEncoder());

        final io.xdb.core.state.StateDecision stateDecision = state.execute(
            input,
            new AsyncStateExecuteFeatures(communication, request.getCommandResults())
        );

        return new AsyncStateExecuteResponse()
            .stateDecision(toApiModel(request.getProcessType(), stateDecision))
            .publishToLocalQueue(communication.getLocalQueueMessagesToPublish());
    }

    private StateDecision toApiModel(final String processType, final io.xdb.core.state.StateDecision stateDecision) {
        if (stateDecision.getStateDecision() != null) {
            return stateDecision.getStateDecision();
        }

        final List<StateMovement> stateMovements = stateDecision
            .getNextStates()
            .stream()
            .map(stateMovement ->
                new StateMovement()
                    .stateId(stateMovement.getStateId())
                    .stateInput(workerServiceOptions.getObjectEncoder().encode(stateMovement.getStateInput()))
                    .stateConfig(
                        ProcessUtil.getAsyncStateConfig(
                            registry.getProcessState(processType, stateMovement.getStateId())
                        )
                    )
            )
            .collect(Collectors.toList());

        return new StateDecision().nextStates(stateMovements);
    }
}
