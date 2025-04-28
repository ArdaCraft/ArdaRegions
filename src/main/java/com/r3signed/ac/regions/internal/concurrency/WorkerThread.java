package com.r3signed.ac.regions.internal.concurrency;

import com.r3signed.ac.regions.ArdaRegions;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

// TODO: Add alternative pools, e.g. ForkJoin, for more complex tasks
@ApiStatus.Internal
public class WorkerThread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArdaRegions.MOD_NAME + "/WorkerThread");
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    /*
     * Shutdown hook to ensure the thread is terminated on exit.
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down worker thread");
            EXECUTOR.shutdown();
            LOGGER.info("Worker thread pool shutdown");
        }));
    }

    /**
     * Submits a task to the worker thread.
     *
     * @param task The task to run
     * @return A {@link Future} representing the result of the task
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return EXECUTOR.submit(task);
    }

    /**
     * Executes a task on the worker thread.
     *
     * @param task The task to run
     */
    public static <T> void execute(Callable<T> task) {
        execute(task, null, null);
    }

    /**
     * Executes a task on the worker thread.
     *
     * @param task The task to run
     * @param onSuccess The callback to run on success
     */
    public static <T> void execute(Callable<T> task, Consumer<T> onSuccess) {
        execute(task, onSuccess, null);
    }

    /**
     * Executes a task on the worker thread.
     *
     * @param task The task to run
     * @param onSuccess The callback to run on success
     * @param onFail The callback to run on failure
     */
    public static <T> void execute(Callable<T> task, Consumer<T> onSuccess, Consumer<Exception> onFail) {
        EXECUTOR.submit(() -> {
            try {
                T result = task.call();
                if (onSuccess != null) {
                    onSuccess.accept(result);
                }
            } catch (Exception e) {
                LOGGER.error("Error executing task on worker thread", e);
                if (onFail != null) {
                    onFail.accept(e);
                }
            }
        });
    }

    /**
     * @return True if the calling thread is the worker thread, otherwise false
     */
    public static boolean isWorkerThread() {
        return Thread.currentThread() == EXECUTOR;
    }
}
