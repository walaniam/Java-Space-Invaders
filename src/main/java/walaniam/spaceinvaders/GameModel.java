package walaniam.spaceinvaders;

import com.zetcode.Commons;
import com.zetcode.sprite.Alien;
import com.zetcode.sprite.Player;
import com.zetcode.sprite.Shot;
import lombok.Getter;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class GameModel {

    private final List<Alien> aliens;
    private final Player player;
    private Shot shot;

    public GameModel() {
        this.aliens = Collections.unmodifiableList(newAliens());
        this.player = new Player();
        this.shot = new Shot();
    }

    public boolean drawAll(Graphics g, ImageObserver observer) {
        drawAliens(g, observer);
        boolean inGame = drawPlayer(g, observer);
        shot.draw(g, observer);
        drawBombing(g, observer);
        return inGame;
    }

    private void drawAliens(Graphics g, ImageObserver observer) {
        aliens.forEach(alien -> {
            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), observer);
            }
            if (alien.isDying()) {
                alien.die();
            }
        });
    }

    private boolean drawPlayer(Graphics g, ImageObserver observer) {
        boolean inGame = true;
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), observer);
        }
        if (player.isDying()) {
            player.die();
            inGame = false;
        }
        return inGame;
    }

    private void drawBombing(Graphics g, ImageObserver observer) {
        aliens.forEach(alien -> {
            var b = alien.getBomb();
            if (b.isVisible()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), observer);
            }
        });
    }

    public void shotFired() {
        if (!shot.isVisible()) {
            shot = new Shot(player.getX(), player.getY());
        }
    }

    private List<Alien> newAliens() {

        var aliens = new ArrayList<Alien>();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
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
