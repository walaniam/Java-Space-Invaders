package walaniam.spaceinvaders.model;

import com.zetcode.Commons;
import com.zetcode.sprite.Alien;
import com.zetcode.sprite.Player;
import com.zetcode.sprite.PlayerDescriptor;
import com.zetcode.sprite.Sprite;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static com.zetcode.Commons.*;

@Getter
@EqualsAndHashCode
public class GameModelImpl implements GameModel {

    private final GameState state = new InternalState();
    private final List<Alien> aliens;
    private final Player player;
    private Player playerTwo;

    public GameModelImpl() {
        this.aliens = newAliens();
        this.player = Player.playerOne(state);
        this.playerTwo = Player.playerTwo(state);
    }

    @Override
    public void drawAll(Graphics g, ImageObserver observer) {
        aliens.forEach(alien -> alien.draw(g, observer));
        player.draw(g, observer);
        playerTwo.draw(g, observer);
        drawBombing(g, observer);
    }

    @Override
    public void mergeWith(GameModel other) {
        // TODO calculate player two shots
        this.playerTwo = other.getPlayerTwo();
        IntStream.range(0, NUMBER_OF_ALIENS_TO_DESTROY).forEach(i -> {
            if (other.getAliens().get(i).isDying()) {
                aliens.get(i).setDying(true);
            }
        });
        other.getGameEndCause().ifPresent(this::gameEnd);
        if (!player.isVisible()) {
            state.getPlayers().remove(player.getDescriptor());
        }
        if (!playerTwo.isVisible()) {
            state.getPlayers().remove(playerTwo.getDescriptor());
        }
    }

    private void drawBombing(Graphics g, ImageObserver observer) {
        aliens.stream()
                .map(Alien::getBomb)
                .filter(Sprite::isVisible)
                .forEach(bomb -> bomb.draw(g, observer));
    }

    private List<Alien> newAliens() {
        var aliens = new ArrayList<Alien>(NUMBER_OF_ALIENS_TO_DESTROY);
        for (int i = 0; i < ALIENS_ROWS; i++) {
            for (int j = 0; j < ALIENS_COLUMNS; j++) {
                var alien = new Alien(
                        Commons.ALIEN_INIT_X + 18 * j,
                        Commons.ALIEN_INIT_Y + 18 * i
                );
                aliens.add(alien);
            }
        }
        return aliens;
    }

    @Override
    public int getDeaths() {
        return state.getDeaths();
    }

    @Override
    public void plusDeath() {
        state.plusDeath();
    }

    @Override
    public void setAlienDirection(int direction) {
        state.setAlienDirection(direction);
    }

    @Override
    public int getAlienDirection() {
        return state.getAlienDirection();
    }

    @Override
    public void gameEnd(GameEndCause cause) {
        state.gameEnd(cause);
    }

    @Override
    public Optional<GameEndCause> getGameEndCause() {
        return state.getGameEndCause();
    }

    @Override
    public Set<PlayerDescriptor> getPlayers() {
        return state.getPlayers();
    }
}
