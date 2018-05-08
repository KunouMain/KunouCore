package samophis.kunou.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.kunou.core.exceptions.ModuleException;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The executor class that submits new Module Threads, sets them up, handles the executor service and the thread counter.
 *
 * @author SamOphis
 * @since 0.1
 */

public class ModuleThreadExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleThreadExecutor.class);
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final ExecutorService SERVICE = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName("ModuleThread-" + COUNTER.getAndIncrement());
        thread.setDaemon(false);
        thread.setUncaughtExceptionHandler((thrd, thrw) -> {
            LOGGER.error("Uncaught Exception in {}: {}", thrd.getName(), thrw.getMessage());
            throw new ModuleException(thrw);
        });
        return thread;
    });
    public static void runModuleMethod(@Nonnull Runnable moduleMethod, boolean shouldDecrementCounter) {
        SERVICE.submit(Objects.requireNonNull(moduleMethod));
        if (shouldDecrementCounter)
            COUNTER.decrementAndGet();
    }
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down ModuleThreadExecutor!");
            if (!SERVICE.isShutdown())
                SERVICE.shutdownNow();
        }));
    }
    private ModuleThreadExecutor() {}
}
