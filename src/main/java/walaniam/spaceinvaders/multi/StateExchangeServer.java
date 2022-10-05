package walaniam.spaceinvaders.multi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static walaniam.spaceinvaders.multi.IpUtils.detectLocalHostAddress;

@RequiredArgsConstructor
@Slf4j
public class StateExchangeServer implements Closeable {

    private final AtomicBoolean opened = new AtomicBoolean();
    private final CountDownLatch serverListening = new CountDownLatch(1);
    private final int port = 17777;

    private final Consumer<GameModel> readConsumer;
    private final Supplier<GameModel> writeSupplier;

    private SocketDataReadWrite socketData;
    private Thread serverThread;
    private volatile ServerSocket serverSocket;

    public void open() {

        if (opened.get()) {
            throw new RuntimeException("Already running");
        }

        if (opened.compareAndSet(false, true)) {
            serverThread = new Thread(() -> {
                try {
                    listen();
                } catch (IOException e) {
                    opened.set(false);
                }
            });
            serverThread.setName("game-server");
            serverThread.start();

            try {
                serverListening.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void listen() throws IOException {

        String ip = detectLocalHostAddress();
        log.info("localhost address: {}", ip);
        serverSocket = new ServerSocket(port, -1, InetAddress.getByName(ip));
        log.info("Opened server socket: {}", serverSocket);
        serverListening.countDown();

        while (opened.get()) {
            try (Socket socket = serverSocket.accept()) {
                log.info("Connected {}", socket);
                socketData = new SocketDataReadWrite(socket, readConsumer, writeSupplier);
                socketData.startListening();
                socketData.awaitTillRunning();
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (opened.compareAndSet(true, false)) {
            log.info("Closing...");
            if (socketData != null) {
                socketData.close();
            }
            serverSocket.close();
        }
    }
}
