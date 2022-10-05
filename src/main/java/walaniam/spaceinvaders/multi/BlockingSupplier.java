package walaniam.spaceinvaders.multi;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;

public class BlockingSupplier<T> implements Supplier<T> {

    private final LinkedBlockingDeque<T> queue = new LinkedBlockingDeque<>(1);

    public void set(T element) {
        queue.offerLast(element);
    }

    @Override
    public T get() {
        try {
            return queue.takeFirst();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
