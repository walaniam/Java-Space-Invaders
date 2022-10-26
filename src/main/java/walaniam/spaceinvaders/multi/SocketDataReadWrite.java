package walaniam.spaceinvaders.multi;

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
public class SocketDataReadWrite implements Closeable {

    private static final int BUFFER_SIZE = 4 * 1024;

    private final AtomicBoolean isReading = new AtomicBoolean();
    private final AtomicBoolean isWriting = new AtomicBoolean();
    private final DataSerializer serializer = new DataSerializer();
    private final CountDownLatch readStartedLatch = new CountDownLatch(1);
    private final CountDownLatch writeStartedLatch = new CountDownLatch(1);
    private final CountDownLatch isRunningLatch = new CountDownLatch(1);

    private final Socket socket;
    private final Consumer<GameModel> remoteRead;
    private final Supplier<GameModel> remoteWrite;
    private final CountDownLatch readWaitsForFirstWrite;

    public SocketDataReadWrite(Socket socket, Consumer<GameModel> remoteRead, Supplier<GameModel> remoteWrite,
                               boolean startReadingAfterWrite) {
        this.socket = socket;
        this.remoteRead = remoteRead;
        this.remoteWrite = remoteWrite;
        this.readWaitsForFirstWrite = startReadingAfterWrite ? new CountDownLatch(1) : new CountDownLatch(0);
    }

    public void startListening() {

        var readerThread = new Thread(() -> {
            readStartedLatch.countDown();
            try {
                readWaitsForFirstWrite.await();
                isReading.set(true);
                readSocket();
            } catch (Exception e) {
                log.error("Could not open socket: " + socket, e);
                isReading.set(false);
                isRunningLatch.countDown();
            }
        }, "game-state-reader");

        var writerThread = new Thread(() -> {
            writeStartedLatch.countDown();
            isWriting.set(true);
            try {
                writeSocket();
            } catch (Exception e) {
                log.error("Could not open socket: " + socket, e);
                isWriting.set(false);
                isRunningLatch.countDown();
            }
        }, "game-state-writer");

        try {
            writerThread.start();
            writeStartedLatch.await();
            readerThread.start();
            readStartedLatch.await();
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
        while (isReading.get()) {
            try {
                // READ FROM CLIENT
                int bytesRead = inputStream.read(buffer);
                log.debug("Reading {} bytes", bytesRead);
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
            } catch (SocketException e) {
                log.error("Failed reading from socket: " + socket, e);
                throw e;
            } catch (IOException e) {
                log.error("Failed reading from socket: " + socket, e);
            }
        }
    }

    private void notifyListener(byte[] data) {
        GameModelImpl model = serializer.deserialize(data, GameModelImpl.class);
        if (model != null) {
            remoteRead.accept(model);
        }
    }

    private void writeSocket() throws IOException {

        var outputStream = socket.getOutputStream();

        log.info("Start writing to socket...");

        while (isWriting.get()) {
            try {
                // WRITE TO CLIENT
                var data = remoteWrite.get();
                if (log.isTraceEnabled()) {
                    log.trace("Writing {}", data);
                }
                byte[] dataBytes = serializer.serialize(data);
                if (log.isDebugEnabled()) {
                    log.debug("Writing {} bytes", dataBytes.length);
                }
                outputStream.write(dataBytes);
                outputStream.flush();
                readWaitsForFirstWrite.countDown();
            } catch (SocketException e) {
                log.error("Failed writing to socket: " + socket, e);
                throw e;
            } catch (IOException e) {
                log.error("Failed writing to socket: " + socket, e);
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
    public void close() {
        try {
            isWriting.set(false);
            isReading.set(false);
            socket.close();
        } catch (IOException e) {
            log.error("Failed on closing", e);
        } finally {
            isRunningLatch.countDown();
        }
    }
}
