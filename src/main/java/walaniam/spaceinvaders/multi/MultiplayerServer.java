package walaniam.spaceinvaders.multi;

import lombok.Getter;
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

@Slf4j
public class MultiplayerServer implements Closeable {

    private final AtomicBoolean opened = new AtomicBoolean();
    private final CountDownLatch serverListening = new CountDownLatch(1);
    private final int port = 17777;

    /**
     * Reads remote model from the client
     */
    private final Consumer<GameModel> remoteRead;
    /**
     * Supplies local model to the client
     */
    private final Supplier<GameModel> remoteWrite;

    private SocketDataReadWrite socketData;
    private Thread serverThread;
    private volatile ServerSocket serverSocket;
    @Getter
    private volatile boolean clientConnected;

    public MultiplayerServer(MultiplayerContext multiplayerContext) {
        this.remoteRead = multiplayerContext.getRemoteRead();
        this.remoteWrite = multiplayerContext.getRemoteWrite();
    }

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
                socketData = new SocketDataReadWrite(socket, remoteRead, remoteWrite);
                socketData.startListening();
                clientConnected = true;
                socketData.awaitTillRunning();
            } catch (Exception e) {
                e.printStackTrace();
                if (socketData != null) {
                    socketData.close();
                }
                clientConnected = false;
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
