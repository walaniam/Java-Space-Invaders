package walaniam.spaceinvaders.multi;

import lombok.Getter;
import lombok.NoArgsConstructor;
import walaniam.spaceinvaders.model.GameModel;

import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor
@Getter
public class MultiplayerContext {

    private final AtomicReference<GameModel> modelRef = new AtomicReference<>();
    /**
     * Consumes model from remote
     */
    private final BlockingExchange<GameModel> remoteRead = new BlockingExchange<>();
    /**
     * Writes mode to remote
     */
    private final BlockingExchange<GameModel> remoteWrite = new BlockingExchange<>();

    public MultiplayerContext(GameModel model) {
        this.modelRef.set(model);
    }
}
