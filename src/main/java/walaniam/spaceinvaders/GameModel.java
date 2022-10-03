package walaniam.spaceinvaders;

import com.zetcode.Commons;
import com.zetcode.sprite.Alien;
import com.zetcode.sprite.Player;
import com.zetcode.sprite.Sprite;
import lombok.Getter;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zetcode.Commons.*;

@Getter
public class GameModel {

    private final List<Alien> aliens;
    private final Player player;

    public GameModel(GameState state) {
        this.aliens = Collections.unmodifiableList(newAliens());
        this.player = new Player(state);
    }

    public void drawAll(Graphics g, ImageObserver observer) {
        aliens.forEach(alien -> alien.draw(g, observer));
        player.draw(g, observer);
        drawBombing(g, observer);
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
}
