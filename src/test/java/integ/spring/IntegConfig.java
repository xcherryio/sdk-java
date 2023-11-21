package integ.spring;

import io.xcherry.core.client.Client;
import io.xcherry.core.client.ClientOptions;
import io.xcherry.core.process.Process;
import io.xcherry.core.registry.Registry;
import io.xcherry.core.worker.WorkerService;
import io.xcherry.core.worker.WorkerServiceOptions;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegConfig {

    public static Client client;

    @Bean
    public Registry registry() {
        return new Registry();
    }

    @Bean
    public static Client client(final Registry registry) {
        client = new Client(registry, ClientOptions.getDefaultLocal());
        // the unit test could not autowire the Client
        return client;
    }

    @Bean
    public WorkerService workerService(final Registry registry, final Process... processes) {
        Arrays.stream(processes).forEach(registry::addProcess);
        return new WorkerService(registry, WorkerServiceOptions.getDefault());
    }
}
