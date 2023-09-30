package io.xdb.core.client;

import io.xdb.core.process.BasicClientProcessOptions;
import io.xdb.core.process.ProcessOptions;
import io.xdb.core.registry.Registry;
import io.xdb.core.state.AsyncState;
import io.xdb.gen.models.AsyncStateConfig;
import java.util.Optional;

public class Client {

    private final Registry registry;
    private final ClientOptions clientOptions;

    private final BasicClient basicClient;

    public Client(final Registry registry, final ClientOptions clientOptions) {
        this.registry = registry;
        this.clientOptions = clientOptions;
        this.basicClient = new BasicClient(clientOptions);
    }

    public String startProcess(
        final String processType,
        final String processId,
        final Object input,
        final ProcessOptions processOptions
    ) {
        AsyncStateConfig asyncStateConfig = null;
        String startingStateId = "";

        final Optional<AsyncState> startingState = registry.getProcessStartingState(processType);
        if (startingState.isPresent()) {
            asyncStateConfig =
                new AsyncStateConfig().skipWaitUntil(AsyncState.shouldSkipWaitUntil(startingState.get()));
            startingStateId = startingState.get().getId();
        }

        final BasicClientProcessOptions basicClientProcessOptions = new BasicClientProcessOptions(
            processOptions,
            asyncStateConfig
        );

        return basicClient.startProcess(processType, processId, startingStateId, input, basicClientProcessOptions);
    }
}
