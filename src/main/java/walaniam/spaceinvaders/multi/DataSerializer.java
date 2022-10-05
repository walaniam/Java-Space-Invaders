package walaniam.spaceinvaders.multi;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;

public class DataSerializer {

    private final Kryo kryo = new Kryo();

    public DataSerializer() {
        kryo.setRegistrationRequired(false);
    }

    public byte[] serialize(Object data) {
        try (var out = new Output(new ByteArrayOutputStream())) {
            kryo.writeObject(out, data);
            return out.toBytes();
        }
    }

    public <T> T deserialize(byte[] bytes, Class<T> type) {
        try (var input = new Input(bytes)) {
            return kryo.readObject(input, type);
        }
    }
}
