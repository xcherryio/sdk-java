package io.xdb.core.registry;

import io.xdb.core.exception.ProcessDefinitionException;
import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.core.state.StateSchema;
import io.xdb.core.utils.ProcessUtil;
import java.util.HashMap;
import java.util.Map;

public class Registry {

    // process type: process
    private final Map<String, Process> processStore = new HashMap<>();
    // process type: { stateId: state }
    private final Map<String, Map<String, AsyncState>> processStatesStore = new HashMap<>();

    public void addProcesses(final Process... processes) {
        for (final Process process : processes) {
            addProcess(process);
        }
    }

    public void addProcess(final Process process) {
        registerProcess(process);
        registerProcessStates(process);
    }

    public Process getProcess(final String type) {
        if (!processStore.containsKey(type)) {
            throw new ProcessDefinitionException(
                String.format("Process type %s has not been registered in processStore.", type)
            );
        }
        return processStore.get(type);
    }

    public AsyncState getProcessState(final String type, final String stateId) {
        if (!processStatesStore.containsKey(type) || !processStatesStore.get(type).containsKey(stateId)) {
            throw new ProcessDefinitionException(
                String.format(
                    "Process type %s or state id %s has not been registered in processStatesStore.",
                    type,
                    stateId
                )
            );
        }
        return processStatesStore.get(type).get(stateId);
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
}
