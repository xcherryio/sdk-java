package integ;

import java.time.Duration;

public class TestUtils {

    public static void sleep(final int seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
