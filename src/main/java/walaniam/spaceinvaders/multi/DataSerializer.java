package walaniam.spaceinvaders.multi;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataSerializer {

    private final Kryo kryo = new Kryo();

    public DataSerializer() {
        kryo.setRegistrationRequired(false);
    }

    public byte[] serialize(Object data) throws IOException {
        try (var bytes = new ByteArrayOutputStream()) {
            var out = new Output(bytes);
            kryo.writeObject(out, data);
            return bytes.toByteArray();
        }
    }

    public <T> T deserialize(byte[] bytes, Class<T> type) {
        var input = new Input(bytes);
        return kryo.readObject(input, type);
    }
}
