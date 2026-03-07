package com.example.LLD.PubSubOffset.models;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bridges a subscriber to a specific topic.
 * Maintains a per-subscriber offset (cursor) and runs a dedicated consumer
 * thread
 * that processes messages sequentially, sleeping when caught up.
 *
 * Thread-safety:
 * - AtomicInteger offset for lock-free cursor tracking.
 * - synchronized(this) + wait/notify for efficient sleep/wakeup.
 */
public class TopicSubscriber {
    private final AtomicInteger offset;
    private final ISubscriber subscriber;

    public TopicSubscriber(ISubscriber subscriber) {
        this.subscriber = subscriber;
        this.offset = new AtomicInteger(0);
    }

    public ISubscriber getSubscriber() {
        return subscriber;
    }

    public AtomicInteger getOffset() {
        return offset;
    }

    /**
     * Starts consuming messages from the given topic using the provided executor
     * service.
     * The task loops forever:
     * 1. While offset < topic.size(), deliver message and increment offset.
     * 2. When caught up, wait() until wakeup() is called.
     */
    public void startConsuming(Topic topic, ExecutorService executorService) {
        executorService.submit(() -> {
            while (true) {
                int currentOffset;
                Message message = null;

                synchronized (this) {
                    // Wait until there are new messages to consume
                    while (offset.get() >= topic.getMessages().size()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.out.println("Consumer thread interrupted for subscriber: "
                                    + subscriber.getId());
                            return;
                        }
                    }
                    // Read and advance offset atomically within the lock
                    currentOffset = offset.getAndIncrement();
                    message = topic.getMessages().get(currentOffset);
                }

                // Deliver outside the lock to avoid holding it during slow subscribers
                subscriber.onMessage(message);
            }
        });
    }

    /**
     * Called by the publisher to wake up this consumer when a new message is added.
     */
    public synchronized void wakeup() {
        notify();
    }

    /**
     * Resets the offset so the subscriber re-processes all messages from the
     * beginning.
     */
    public void resetOffset() {
        offset.set(0);
        wakeup();
    }
}
