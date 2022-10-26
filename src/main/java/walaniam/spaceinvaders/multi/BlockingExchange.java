package walaniam.spaceinvaders.multi;

import lombok.ToString;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ToString
public class BlockingExchange<T> implements Supplier<T>, Consumer<T> {

    private final LinkedBlockingDeque<T> queue = new LinkedBlockingDeque<>(1);

    @Override
    public T get() {
        try {
            return queue.takeFirst();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public T get(long time, TimeUnit unit) {
        try {
            return queue.pollFirst(time, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(T element) {
        queue.offerLast(element);
    }
}
