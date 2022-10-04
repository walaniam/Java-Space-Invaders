package walaniam.spaceinvaders;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import walaniam.spaceinvaders.model.GameModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class StateExchange {

    private final Kryo kryo = new Kryo();

    private final Socket clientSocket;

    public StateExchange() {
        kryo.setRegistrationRequired(false);
        try {
            clientSocket = new Socket("192.168.0.192", 7777);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(GameModel model) {
        var bytes = new ByteArrayOutputStream();
        var out = new Output(bytes);
        kryo.writeObject(out, model);
        out.close();
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            byte[] bytesArr = bytes.toByteArray();
            System.out.println("Writing: " + bytesArr.length);
            outputStream.write(bytesArr);
            outputStream.flush();
            System.out.println("Flushed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
