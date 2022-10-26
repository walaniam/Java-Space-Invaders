package walaniam.spaceinvaders.multi;

import org.junit.jupiter.api.Test;
import walaniam.spaceinvaders.model.GameModelImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataSerializerTest {

    private final DataSerializer underTest = new DataSerializer();

    @Test
    void shouldSerializeAndDeserialize() {
        var model = new GameModelImpl();
        byte[] bytes = underTest.serialize(model);
        var modelDeserialized = underTest.deserialize(bytes, GameModelImpl.class);
        assertEquals(model, modelDeserialized);
    }
}