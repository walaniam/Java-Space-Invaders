package walaniam.spaceinvaders.multi;

import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class StateExchangeClient implements Closeable {

    private final String host;
    private final int port;
    private final Consumer<GameModel> remoteRead;
    private final Supplier<GameModel> remoteWrite;

    private SocketDataReadWrite socketData;

    /**
     *
     * @param serverAddress
     * @param remoteRead consumes model from remote
     * @param remoteWrite writes model to remote
     */
    public StateExchangeClient(String serverAddress, Consumer<GameModel> remoteRead, Supplier<GameModel> remoteWrite) {
        this.host = serverAddress.split(":")[0];
        this.port = Integer.parseInt(serverAddress.split(":")[1]);
        this.remoteRead = remoteRead;
        this.remoteWrite = remoteWrite;
    }

    public void open() {
        try {
            var socket = new Socket(host, port);
            socketData = new SocketDataReadWrite(socket, remoteRead, remoteWrite);
            socketData.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (socketData != null) {
            socketData.close();
        }
    }
}
