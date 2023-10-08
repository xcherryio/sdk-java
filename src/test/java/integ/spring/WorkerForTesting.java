package integ.spring;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.SpringApplication;

public class WorkerForTesting {

    public static final int WORKER_PORT = 8802;

    final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void start() throws ExecutionException, InterruptedException {
        System.getProperties().put("server.port", WORKER_PORT);

        executor
            .submit(() -> {
                SpringApplication.run(SpringMainApplication.class);
            })
            .get();
    }
}
