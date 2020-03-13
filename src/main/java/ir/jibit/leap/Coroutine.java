package ir.jibit.leap;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Consumer;

/**
 * A simple coroutine implementation. A coroutine is a lightweight thread-like processing unit.
 * Coroutine implementation consumes an input and optionally returns back the result in a channel if provided.
 *
 * @param <I> - the input type
 * @author Alireza Pourtaghi
 */
public abstract class Coroutine<I> extends ForkJoinTask<Void> implements Consumer<I> {
    /**
     * A reference to provided input for internal use.
     */
    private I input;

    protected Coroutine(I input) {
        this.input = input;
    }

    /**
     * Non overridable execution implementation to force a single desired task in all coroutine implementations.
     */
    protected final boolean exec() {
        accept(input);
        return true;
    }

    /**
     * Always returns {@code null}.
     *
     * @return {@code null} always
     */
    public final Void getRawResult() {
        return null;
    }

    /**
     * Requires null completion value.
     */
    protected final void setRawResult(Void mustBeNull) {
    }

    /**
     * Static helper method that converts a consumer implementation to a coroutine;
     * Then executes it on the common ForkJoinPool provided by the JVM.
     *
     * @param consumer - main executable unit of work
     * @param input    - input parameter
     * @param <I>      - the input type
     * @throws NullPointerException - in case of null provided consumer
     */
    public static <I> void go(Consumer<I> consumer, I input) {
        Objects.requireNonNull(consumer);

        ForkJoinPool.commonPool().execute(new Coroutine<>(input) {
            @Override
            public void accept(I i) {
                consumer.accept(i);
            }
        });
    }
}
