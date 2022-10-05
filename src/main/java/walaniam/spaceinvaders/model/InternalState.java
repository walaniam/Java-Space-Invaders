package walaniam.spaceinvaders.model;

import com.zetcode.sprite.Direction;
import lombok.Data;

@Data
public class InternalState implements GameState {

    private boolean inGame = true;
    private int deaths;
    private int alienDirection = Direction.LEFT;

    @Override
    public void plusDeath() {
        deaths++;
    }
}
