package integ.spring;

import io.xdb.core.client.Client;
import io.xdb.core.client.ClientOptions;
import io.xdb.core.process.Process;
import io.xdb.core.registry.Registry;
import io.xdb.core.worker.WorkerService;
import io.xdb.core.worker.WorkerServiceOptions;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XdbConfig {

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
