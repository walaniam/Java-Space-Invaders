package walaniam.spaceinvaders.multi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameModelImpl;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class SocketDataReadWrite implements Closeable {

    private static final boolean VERBOSE = false;

    private static final int BUFFER_SIZE = 4 * 1024;

    private final AtomicBoolean opened = new AtomicBoolean();
    private final DataSerializer serializer = new DataSerializer();
    private final CountDownLatch readWriteStartedLatch = new CountDownLatch(2);
    private final CountDownLatch isRunningLatch = new CountDownLatch(1);

    private final Socket socket;
    private final Consumer<GameModel> remoteRead;
    private final Supplier<GameModel> remoteWrite;

    public void startListening() {

        var readerThread = new Thread(() -> {
            readWriteStartedLatch.countDown();
            try {
                readSocket();
            } catch (IOException e) {
                e.printStackTrace();
                opened.set(false);
            }
        }, "game-state-reader");

        var writerThread = new Thread(() -> {
            readWriteStartedLatch.countDown();
            try {
                writeSocket();
            } catch (IOException e) {
                e.printStackTrace();
                opened.set(false);
            }
        }, "game-state-writer");

        readerThread.start();
        writerThread.start();

        try {
            readWriteStartedLatch.await();
            opened.set(true);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Opened");
    }

    private void readSocket() throws IOException {

        var inputStream = socket.getInputStream();

        ByteArrayOutputStream collector = null;
        byte[] buffer = new byte[BUFFER_SIZE];

        log.info("Start reading from socket...");
        while (opened.get()) {
            // READ FROM CLIENT
            int bytesRead = inputStream.read(buffer);
            if (VERBOSE) {
                log.info("Reading {} bytes", bytesRead);
            }
            if (bytesRead > -1 || collector != null) {
                if (collector == null) {
                    collector = new ByteArrayOutputStream(BUFFER_SIZE);
                }
                collector.write(buffer, 0, bytesRead);
                if (bytesRead < BUFFER_SIZE) {
                    notifyListener(collector.toByteArray());
                    collector = null;
                }
            }
        }
    }

    private void notifyListener(byte[] data) {
        GameModelImpl model = serializer.deserialize(data, GameModelImpl.class);
        remoteRead.accept(model);
    }

    private void writeSocket() throws IOException {

        var outputStream = socket.getOutputStream();

        log.info("Start writing to socket...");

        while (opened.get()) {
            // WRITE TO CLIENT
            var data = remoteWrite.get();
            if (VERBOSE) {
                log.info("Writing {}", data);
            }
            byte[] localModelBytes = serializer.serialize(data);
            if (VERBOSE) {
                log.info("Writing {} bytes", localModelBytes.length);
            }
            try {
                outputStream.write(localModelBytes);
                outputStream.flush();
            } catch (SocketException e) {
                log.warn("Socket: ", e);
            }
        }
    }

    public void awaitTillRunning() {
        try {
            isRunningLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            opened.set(false);
            socket.close();
        } finally {
            isRunningLatch.countDown();
        }
    }
}
