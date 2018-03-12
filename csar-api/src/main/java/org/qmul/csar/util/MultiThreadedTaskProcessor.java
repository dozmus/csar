package org.qmul.csar.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A single-use multi-threaded task processor.
 */
public abstract class MultiThreadedTaskProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadedTaskProcessor.class);
    private final int threadCount;
    private final ExecutorService executor;
    private final CountDownLatch finishedLatch;
    private Runnable encapsulatedRunnable;
    private boolean started = false;

    /**
     * Constructs a new {@link MultiThreadedTaskProcessor} with the given arguments.
     *
     * @param threadCount the amount of threads to use
     * @param threadName the thread name to use, this will be suffixed with <tt>-{thread-count}</tt>.
     */
    public MultiThreadedTaskProcessor(int threadCount, String threadName) {
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory(threadName + "-%d"));
        this.finishedLatch = new CountDownLatch(threadCount);
    }

    /**
     * Sets the task to run, this has to be set before {@link #run()}.
     */
    public final void setRunnable(Runnable r) {
        if (encapsulatedRunnable != null)
            throw new IllegalStateException("runnable already set");
        encapsulatedRunnable = () -> {
            try {
                LOGGER.trace("Starting...");
                r.run();
            } finally {
                LOGGER.trace("Finished");
                countDown();
            }
        };
    }

    /**
     * Submits tasks to the underlying thread pool to begin processing, this is a blocking operation.
     * If a fatal error occurs then {@link #terminate()} is called, followed by {@link #onFatalErrorOccurred().}
     *
     * @throws IllegalStateException If {@link #setRunnable(Runnable)} was not called, or this has already finished
     * running, or this has already been started.
     */
    public final void run() {
        // Check if ready to run
        if (encapsulatedRunnable == null) {
            throw new IllegalStateException("task was not set");
        } else if (runningCount() == 0) {
            throw new IllegalStateException("already finished running");
        } else if (started) {
            throw new IllegalStateException("already started");
        }
        started = true;

        // Submit tasks
        for (int i = 0; i < threadCount; i++) {
            executor.submit(encapsulatedRunnable);
        }

        // Wait for termination
        executor.shutdown();

        try {
            finishedLatch.await();
        } catch (InterruptedException e) {
            String msg = "Error waiting for termination because the current thread was interrupted - terminating tasks.";
            LOGGER.error(msg);
            terminate();
            onFatalErrorOccurred();
        }
    }

    /**
     * This is called when a fatal error occurs, override this in subclasses to specify the behaviour.
     * This is called after {@link #terminate()} is.
     */
    public void onFatalErrorOccurred() {
    }

    /**
     * Calls {@link ExecutorService#shutdownNow()} on {@link #executor}.
     * @see ExecutorService#shutdownNow()
     */
    public final void terminate() {
        executor.shutdownNow();
    }

    private long runningCount() {
        synchronized (finishedLatch) {
            return finishedLatch.getCount();
        }
    }

    private void countDown() {
        synchronized (finishedLatch) {
            finishedLatch.countDown();
        }
    }
}
