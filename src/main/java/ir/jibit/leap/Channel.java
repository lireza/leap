package ir.jibit.leap;

import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A channel implementation for use in CSP style concurrency model.
 *
 * @param <E> - the element type of communication
 * @author Alireza Pourtaghi
 */
public final class Channel<E> extends SynchronousQueue<E> {
    /**
     * Determines whether the channel is closed or not.
     */
    private final AtomicBoolean closed;

    private Channel() {
        closed = new AtomicBoolean(false);
    }

    /**
     * Tries to send an element to the channel; waiting if necessary for another thread to receive it.
     *
     * @param element - the element to send
     * @return {@code true} if sending was successful otherwise {@code false}
     */
    public boolean send(E element) {
        Objects.requireNonNull(element);

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
     * Tries to retrieve an element from the channel; waiting if necessary for another thread to send it.
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
     * Creates and returns a newly created ready to sue channel.
     *
     * @param eClass - class of element type
     * @param <E>    - element type
     * @return newly created and ready to use channel of type E
     */
    public static <E> Channel<E> make(Class<E> eClass) {
        return new Channel<>();
    }

    /**
     * Creates and returns a newly created ready to sue buffered channel.
     *
     * @param eClass - class of element type
     * @param <E>    - element type
     * @return newly created and ready to use buffered channel of type E
     */
    public static <E> BufferedChannel<E> make(Class<E> eClass, int capacity) {
        return BufferedChannel.make(eClass, capacity);
    }
}
