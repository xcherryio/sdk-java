package io.xcherry.core.worker;

import com.google.common.collect.ImmutableList;
import io.xcherry.core.command.BaseCommand;
import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.persistence.Persistence;
import io.xcherry.core.process.Process;
import io.xcherry.core.registry.Registry;
import io.xcherry.core.rpc.RpcDefinition;
import io.xcherry.core.state.AsyncState;
import io.xcherry.core.utils.ProcessUtil;
import io.xcherry.gen.models.AsyncStateExecuteRequest;
import io.xcherry.gen.models.AsyncStateExecuteResponse;
import io.xcherry.gen.models.AsyncStateWaitUntilRequest;
import io.xcherry.gen.models.AsyncStateWaitUntilResponse;
import io.xcherry.gen.models.CommandRequest;
import io.xcherry.gen.models.LocalQueueCommand;
import io.xcherry.gen.models.ProcessRpcWorkerRequest;
import io.xcherry.gen.models.ProcessRpcWorkerResponse;
import io.xcherry.gen.models.StateDecision;
import io.xcherry.gen.models.StateMovement;
import io.xcherry.gen.models.TimerCommand;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkerService {

    public static final String API_PATH_ASYNC_STATE_WAIT_UNTIL = "/api/v1/xcherry/worker/async-state/wait-until";
    public static final String API_PATH_ASYNC_STATE_EXECUTE = "/api/v1/xcherry/worker/async-state/execute";
    public static final String API_PATH_PROCESS_RPC = "/api/v1/xcherry/worker/process/rpc";

    private final Registry registry;
    private final WorkerServiceOptions workerServiceOptions;

    public AsyncStateWaitUntilResponse handleAsyncStateWaitUntil(final AsyncStateWaitUntilRequest request) {
        final AsyncState state = registry.getProcessState(request.getProcessType(), request.getStateId());
        final Object input = workerServiceOptions
            .getObjectEncoder()
            .decodeFromEncodedObject(request.getStateInput(), state.getInputType());

        final Communication communication = new Communication(workerServiceOptions.getObjectEncoder());

        final io.xcherry.core.command.CommandRequest commandRequest = state.waitUntil(
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
            .decodeFromEncodedObject(request.getStateInput(), state.getInputType());

        final Communication communication = new Communication(workerServiceOptions.getObjectEncoder());
        final Persistence persistence = new Persistence(
            request.getAppDatabaseReadResponse(),
            registry.getPersistenceSchema(request.getProcessType()),
            workerServiceOptions.getDatabaseStringEncoder()
        );

        final io.xcherry.core.state.StateDecision stateDecision = state.execute(
            Context.fromApiModel(request.getContext()),
            input,
            CommandResults.fromApiModel(request.getCommandResults(), workerServiceOptions.getObjectEncoder()),
            persistence,
            communication
        );

        return new AsyncStateExecuteResponse()
            .stateDecision(toApiModel(request.getProcessType(), stateDecision))
            .publishToLocalQueue(communication.getLocalQueueMessagesToPublish());
        //            .writeToGlobalAttributes(
        //                persistence.getGlobalAttributesToUpsert(registry.getPersistenceSchema(request.getProcessType()))
        //            );
    }

    public ProcessRpcWorkerResponse handleProcessRpc(final ProcessRpcWorkerRequest request) {
        final Process process = registry.getProcess(request.getProcessType());
        final Method rpcMethod = registry.getRpcMethod(request.getProcessType(), request.getRpcName());

        final Object input;

        final Class<?> inputType = RpcDefinition.getInputType(rpcMethod);
        if (inputType != null) {
            input = workerServiceOptions.getObjectEncoder().decodeFromEncodedObject(request.getInput(), inputType);
        } else {
            input = null;
        }

        final Communication communication = new Communication(workerServiceOptions.getObjectEncoder());

        final Persistence persistence = new Persistence(
            request.getAppDatabaseReadResponse(),
            registry.getPersistenceSchema(request.getProcessType()),
            workerServiceOptions.getDatabaseStringEncoder()
        );

        final Object output = RpcDefinition.invoke(
            rpcMethod,
            process,
            Context.fromApiModel(request.getContext()),
            input,
            persistence,
            communication
        );

        return new ProcessRpcWorkerResponse()
            .output(workerServiceOptions.getObjectEncoder().encodeToEncodedObject(output))
            .stateDecision(toApiModel(request.getProcessType(), communication.getStateDecision()))
            .publishToLocalQueue(communication.getLocalQueueMessagesToPublish());
        //            .writeToGlobalAttributes(
        //                persistence.getGlobalAttributesToUpsert(registry.getPersistenceSchema(request.getProcessType()))
        //            );
    }

    private StateDecision toApiModel(
        final String processType,
        final io.xcherry.core.state.StateDecision stateDecision
    ) {
        if (stateDecision == null) {
            return null;
        }

        if (stateDecision.getStateDecision() != null) {
            return stateDecision.getStateDecision();
        }

        final List<StateMovement> stateMovements = stateDecision
            .getNextStates()
            .stream()
            .map(stateMovement ->
                new StateMovement()
                    .stateId(stateMovement.getStateId())
                    .stateInput(
                        workerServiceOptions.getObjectEncoder().encodeToEncodedObject(stateMovement.getStateInput())
                    )
                    .stateConfig(
                        ProcessUtil.getAsyncStateConfig(
                            registry.getProcessState(processType, stateMovement.getStateId()),
                            registry.getProcess(processType)
                        )
                    )
            )
            .collect(Collectors.toList());

        return new StateDecision().nextStates(stateMovements);
    }

    private CommandRequest toApiModel(final io.xcherry.core.command.CommandRequest commandRequest) {
        final CommandRequest apiCommandRequest = new CommandRequest().waitingType(commandRequest.getWaitingType());

        final List<BaseCommand> commands = commandRequest.getCommands() == null
            ? ImmutableList.of()
            : commandRequest.getCommands();
        for (final BaseCommand command : commands) {
            if (command instanceof io.xcherry.core.command.TimerCommand) {
                apiCommandRequest.addTimerCommandsItem(
                    new TimerCommand()
                        .delayInSeconds(((io.xcherry.core.command.TimerCommand) command).getDelayInSeconds())
                );
            } else if (command instanceof io.xcherry.core.command.LocalQueueCommand) {
                apiCommandRequest.addLocalQueueCommandsItem(
                    new LocalQueueCommand()
                        .queueName(((io.xcherry.core.command.LocalQueueCommand) command).getQueueName())
                        .count(((io.xcherry.core.command.LocalQueueCommand) command).getCount())
                );
            }
        }

        return apiCommandRequest;
    }
}
