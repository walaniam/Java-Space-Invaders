package walaniam.spaceinvaders.model;

import com.zetcode.sprite.Direction;
import com.zetcode.sprite.PlayerDescriptor;
import lombok.Data;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
public class InternalState implements GameState {

    private Set<PlayerDescriptor> players = new HashSet<>();
    private int deaths;
    private int alienDirection = Direction.LEFT;
    private GameEndCause gameEndCause;

    @Override
    public void plusDeath() {
        deaths++;
    }

    @Override
    public void gameEnd(GameEndCause cause) {
        this.gameEndCause = cause;
    }

    @Override
    public Optional<GameEndCause> getGameEndCause() {
        return Optional.ofNullable(gameEndCause);
    }
}
