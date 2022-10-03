package walaniam.spaceinvaders.model;

import lombok.Data;

@Data
class InternalState implements GameState {

    private boolean inGame = true;
    private int deaths;

    @Override
    public void plusDeath() {
        deaths++;
    }
}
