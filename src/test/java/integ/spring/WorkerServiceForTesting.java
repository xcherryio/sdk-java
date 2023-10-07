package integ.spring;

public class WorkerServiceForTesting {

    private static WorkerForTesting testWorker;

    public static void startWorkerIfNotUp() {
        if (testWorker == null) {
            testWorker = new WorkerForTesting();
            try {
                testWorker.start();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
