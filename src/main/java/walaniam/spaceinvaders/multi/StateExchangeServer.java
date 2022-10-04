package walaniam.spaceinvaders.multi;

import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameModelImpl;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static walaniam.spaceinvaders.multi.IpUtils.detectLocalHostAddress;

@Slf4j
public class StateExchangeServer implements Closeable {

    private static final int BUFFER_SIZE = 4 * 1024;

    private final DataSerializer serializer = new DataSerializer();
    private final AtomicBoolean opened = new AtomicBoolean();
    private final CountDownLatch serverListening = new CountDownLatch(1);
    private final int port = 17777;

    private final Supplier<GameModel> localModelSupplier;

    private Thread serverThread;
    private volatile ServerSocket serverSocket;
    private GameModelListener remoteModelListener;

    public StateExchangeServer(Supplier<GameModel> localModelSupplier) {
        this.localModelSupplier = localModelSupplier;
    }

    public synchronized void open(GameModelListener remoteModelListener) {

        if (opened.get()) {
            throw new RuntimeException("Already running");
        }

        this.remoteModelListener = remoteModelListener;

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
        }
    }

    public void await() throws InterruptedException {
        serverListening.await();
    }

    private void listen() throws IOException {

        String ip = detectLocalHostAddress();
        log.info("localhost address: {}", ip);
        serverSocket = new ServerSocket(port, -1, InetAddress.getByName(ip));
        log.info("Opened server socket: {}", serverSocket);
        serverListening.countDown();

        while (opened.get()) {
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Connected " + socket);

                var inputStream = socket.getInputStream();
                var outputStream = socket.getOutputStream();

                System.out.println("Reading from socket: " + socket);

                ByteArrayOutputStream collector = null;
                byte[] buffer = new byte[BUFFER_SIZE];

                while (opened.get()) {

                    // WRITE TO CLIENT
                    byte[] localModelBytes = serializer.serialize(localModelSupplier.get());
                    outputStream.write(localModelBytes);
                    outputStream.flush();

                    // READ FROM CLIENT
                    int bytesRead = inputStream.read(buffer);
                    System.out.println("Read: " + bytesRead);
                    if (collector == null) {
                        collector = new ByteArrayOutputStream();
                    }
                    collector.write(buffer, 0, bytesRead);
                    if (bytesRead < BUFFER_SIZE) {
                        notifyListener(collector.toByteArray());
                        collector = null;
                    }
                }
            }
        }
    }

    private void notifyListener(byte[] data) {
        GameModel model = serializer.deserialize(data, GameModelImpl.class);
        remoteModelListener.accept(model);
    }

    @Override
    public void close() throws IOException {
        if (opened.compareAndSet(true, false)) {
            serverSocket.close();
        }
    }
}
