package ir.jibit.leap;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.requireNonNull;

/**
 * A buffered channel implementation for use in CSP style concurrency model.
 *
 * @param <E> - the element type of communication
 * @author Alireza Pourtaghi
 */
public final class BufferedChannel<E> extends ArrayBlockingQueue<E> {
    /**
     * Determines whether the channel is closed or not.
     */
    private final AtomicBoolean closed;

    private BufferedChannel(int capacity) {
        super(capacity);
        closed = new AtomicBoolean(Boolean.FALSE);
    }

    /**
     * Tries to send an element to the channel; waiting for space to become available if the queue is full.
     *
     * @param element - the element to send
     * @return {@code true} if sending was successful otherwise {@code false}
     */
    public boolean send(E element) {
        requireNonNull(element);

        try {
            if (closed.get()) {
                return false;
            } else {
                put(element);
                return true;
            }
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * Tries to retrieve an element from the channel; waiting if necessary until an element becomes available.
     *
     * @return {@code null} if retrieving was not successful otherwise the element
     */
    public E retrieve() {
        try {
            if (closed.get()) {
                return null;
            } else {
                return take();
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * Closes current channel.
     */
    public void close() {
        closed.set(Boolean.TRUE);
    }

    /**
     * Creates and returns a newly created ready to use buffered channel.
     *
     * @param eClass   - class of element type
     * @param capacity - capacity of channel
     * @param <E>      - element type
     * @return newly created and ready to use buffered channel of type E
     */
    public static <E> BufferedChannel<E> make(Class<E> eClass, int capacity) {
        return new BufferedChannel<>(capacity);
    }
}
