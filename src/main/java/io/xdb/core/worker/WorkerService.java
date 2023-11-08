package io.xdb.core.worker;

import com.google.common.collect.ImmutableList;
import io.xdb.core.command.BaseCommand;
import io.xdb.core.command.CommandResults;
import io.xdb.core.communication.Communication;
import io.xdb.core.context.Context;
import io.xdb.core.persistence.Persistence;
import io.xdb.core.registry.Registry;
import io.xdb.core.state.AsyncState;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.AsyncStateExecuteRequest;
import io.xdb.gen.models.AsyncStateExecuteResponse;
import io.xdb.gen.models.AsyncStateWaitUntilRequest;
import io.xdb.gen.models.AsyncStateWaitUntilResponse;
import io.xdb.gen.models.CommandRequest;
import io.xdb.gen.models.LocalQueueCommand;
import io.xdb.gen.models.StateDecision;
import io.xdb.gen.models.StateMovement;
import io.xdb.gen.models.TimerCommand;
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

        final io.xdb.core.command.CommandRequest commandRequest = state.waitUntil(
            Context.fromApiModel(request.getContext()),
            input,
            communication
        );

        return new AsyncStateWaitUntilResponse()
            .commandRequest(toApiModel(commandRequest))
            .publishToLocalQueue(communication.getLocalQueueMessagesToPublish());
    }

    public AsyncStateExecuteResponse handleAsyncStateExecute(final AsyncStateExecuteRequest request) {
        final AsyncState state = registry.getProcessState(request.getProcessType(), request.getStateId());
        final Object input = workerServiceOptions
            .getObjectEncoder()
            .decode(request.getStateInput(), state.getInputType());

        final Communication communication = new Communication(workerServiceOptions.getObjectEncoder());
        final Persistence persistence = new Persistence();

        final io.xdb.core.state.StateDecision stateDecision = state.execute(
            Context.fromApiModel(request.getContext()),
            input,
            CommandResults.fromApiModel(request.getCommandResults(), workerServiceOptions.getObjectEncoder()),
            persistence,
            communication
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

    private CommandRequest toApiModel(final io.xdb.core.command.CommandRequest commandRequest) {
        final CommandRequest apiCommandRequest = new CommandRequest().waitingType(commandRequest.getWaitingType());

        final List<BaseCommand> commands = commandRequest.getCommands() == null
            ? ImmutableList.of()
            : commandRequest.getCommands();
        for (final BaseCommand command : commands) {
            if (command instanceof io.xdb.core.command.TimerCommand) {
                apiCommandRequest.addTimerCommandsItem(
                    new TimerCommand().delayInSeconds(((io.xdb.core.command.TimerCommand) command).getDelayInSeconds())
                );
            } else if (command instanceof io.xdb.core.command.LocalQueueCommand) {
                apiCommandRequest.addLocalQueueCommandsItem(
                    new LocalQueueCommand()
                        .queueName(((io.xdb.core.command.LocalQueueCommand) command).getQueueName())
                        .count(((io.xdb.core.command.LocalQueueCommand) command).getCount())
                );
            }
        }

        return apiCommandRequest;
    }
}
