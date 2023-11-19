package io.xdb.core.registry;

import io.xdb.core.exception.ProcessDefinitionException;
import io.xdb.core.persistence.schema.PersistenceSchema;
import io.xdb.core.process.Process;
import io.xdb.core.rpc.RPC;
import io.xdb.core.rpc.RpcDefinition;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateSchema;
import io.xdb.core.utils.ProcessUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Registry {

    // process type: process
    private final Map<String, Process> processStore = new HashMap<>();
    // process type: { stateId: state }
    private final Map<String, Map<String, AsyncState>> processStatesStore = new HashMap<>();
    // process type: { rpcName: rpcMethod }
    private final Map<String, Map<String, Method>> processRpcsStore = new HashMap<>();

    public void addProcesses(final Process... processes) {
        for (final Process process : processes) {
            addProcess(process);
        }
    }

    public void addProcess(final Process process) {
        registerProcess(process);
        registerProcessStates(process);
        registerProcessRpcs(process);
    }

    public Process getProcess(final String type) {
        if (!processStore.containsKey(type)) {
            throw new ProcessDefinitionException(
                String.format("Process type %s has not been registered in processStore.", type)
            );
        }
        return processStore.get(type);
    }

    public AsyncState getProcessState(final String processType, final String stateId) {
        if (!processStatesStore.containsKey(processType) || !processStatesStore.get(processType).containsKey(stateId)) {
            throw new ProcessDefinitionException(
                String.format(
                    "Process type %s or state id %s has not been registered in processStatesStore.",
                    processType,
                    stateId
                )
            );
        }
        return processStatesStore.get(processType).get(stateId);
    }

    public PersistenceSchema getPersistenceSchema(final String processType) {
        if (!processStore.containsKey(processType)) {
            throw new ProcessDefinitionException(
                String.format("Process type %s has not been registered in processStore.", processType)
            );
        }

        return processStore.get(processType).getPersistenceSchema();
    }

    public Method getRpcMethod(final String processType, final String rpcName) {
        if (!processRpcsStore.containsKey(processType) || !processRpcsStore.get(processType).containsKey(rpcName)) {
            throw new ProcessDefinitionException(
                String.format(
                    "Process type %s or rpc method %s has not been registered in processRpcsStore.",
                    processType,
                    rpcName
                )
            );
        }
        return processRpcsStore.get(processType).get(rpcName);
    }

    private void registerProcess(final Process process) {
        if (process.getOptions() != null) {
            process.getOptions().validate();
        }

        final String type = ProcessUtil.getProcessType(process);

        if (processStore.containsKey(type)) {
            throw new ProcessDefinitionException(
                String.format("Process type %s has previously been registered in processStore.", type)
            );
        }

        processStore.put(type, process);
    }

    private void registerProcessStates(final Process process) {
        final StateSchema stateSchema = process.getStateSchema() == null ||
            process.getStateSchema().getAllStates() == null
            ? StateSchema.noStartingState()
            : process.getStateSchema();

        final String processType = ProcessUtil.getProcessType(process);

        final HashMap<String, AsyncState> stateMap = new HashMap<>();

        for (final AsyncState state : stateSchema.getAllStates()) {
            if (state.getOptions() != null) {
                state.getOptions().validate();
            }

            final String stateId = ProcessUtil.getStateId(state);

            if (stateMap.containsKey(stateId)) {
                throw new ProcessDefinitionException(
                    String.format(
                        "State id %s has previously been registered in process type %s.",
                        stateId,
                        processType
                    )
                );
            }
            stateMap.put(stateId, state);
        }

        processStatesStore.put(processType, stateMap);
    }

    private void registerProcessRpcs(final Process process) {
        final String processType = ProcessUtil.getProcessType(process);

        final Map<String, Method> rpcMap = new HashMap<>();

        Arrays
            .stream(process.getClass().getMethods())
            .filter(method -> method.isAnnotationPresent(RPC.class))
            .forEach(rpcMethod -> {
                RpcDefinition.validate(rpcMethod);
                rpcMap.put(rpcMethod.getName(), rpcMethod);
            });

        processRpcsStore.put(processType, rpcMap);
    }
}
