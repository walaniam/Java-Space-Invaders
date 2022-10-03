package walaniam.spaceinvaders.model;

public interface GameState {

    void setInGame(boolean inGame);
    boolean isInGame();
    int getDeaths();
    void plusDeath();
}
