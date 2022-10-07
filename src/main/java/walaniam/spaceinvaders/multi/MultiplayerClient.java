package walaniam.spaceinvaders.multi;

import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class MultiplayerClient implements Closeable {

    private final String host;
    private final int port;
    private final Consumer<GameModel> remoteRead;
    private final Supplier<GameModel> remoteWrite;

    private SocketDataReadWrite socketData;

    public MultiplayerClient(String serverAddress, MultiplayerContext multiplayerContext) {
        this.host = serverAddress.split(":")[0];
        this.port = Integer.parseInt(serverAddress.split(":")[1]);
        this.remoteRead = multiplayerContext.getRemoteRead();
        this.remoteWrite = multiplayerContext.getRemoteWrite();
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
    public void close() {
        if (socketData != null) {
            socketData.close();
        }
    }
}
