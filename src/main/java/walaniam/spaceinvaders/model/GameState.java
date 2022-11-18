package walaniam.spaceinvaders.model;

import com.zetcode.sprite.PlayerDescriptor;

import java.util.Optional;
import java.util.Set;

public interface GameState {

    enum GameEndCause {
        INVASION, WIN
    }

    Set<PlayerDescriptor> getPlayers();

    int getDeaths();

    void plusDeath();

    void gameEnd(GameEndCause cause);

    Optional<GameEndCause> getGameEndCause();

    void setAlienDirection(int direction);

    int getAlienDirection();

    default boolean isInGame() {
        int playersCount = getPlayers().size();
        return getGameEndCause().isEmpty() && playersCount > 0;
    }
}
