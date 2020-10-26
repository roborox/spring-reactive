package ru.roborox.reactive.test.wait;

import kotlin.KotlinNullPointerException;
import org.junit.jupiter.api.Assertions;
import ru.roborox.reactive.utils.RunnableWithException;

import java.util.concurrent.Callable;

public class Wait {
    private Wait() {

    }

    public static <V> V waitFor(Callable<V> callable) {
        return waitFor(5000, callable);
    }

    public static <V> V waitFor(long timeout, Callable<V> callable) {
        long start = System.currentTimeMillis();
        while((System.currentTimeMillis() - start) < timeout) {
            try {
                V value = callable.call();
                if(value != null) {
                    return value;
                }
                Thread.sleep(500);
            } catch (Exception ignored) {
            }
        }
        Assertions.fail("Failed wait " + callable);
        return null;
    }

    public static void waitAssert(RunnableWithException runnable) throws Exception {
        waitAssert(5000, runnable);
    }

    public static void waitAssert(long timeout, RunnableWithException runnable) throws Exception {
        final long maxTime = System.currentTimeMillis() + timeout;
        while (true) {
            try {
                runnable.run();
                return;
            } catch (AssertionError | KotlinNullPointerException e) {
                if (System.currentTimeMillis() > maxTime) {
                    throw e;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {

                }
            }
        }
    }
}
