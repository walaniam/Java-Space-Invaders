package walaniam.spaceinvaders.multi;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockingExchange<T> implements Supplier<T>, Consumer<T> {

    private final LinkedBlockingDeque<T> queue = new LinkedBlockingDeque<>(1);

    @Override
    public T get() {
        try {
//            return queue.pollFirst(100, TimeUnit.SECONDS);
            return queue.takeFirst();
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
