package walaniam.spaceinvaders.model;

import com.zetcode.sprite.Alien;
import com.zetcode.sprite.Player;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.List;

public interface GameModel extends GameState {
    Player getPlayer();

    Player getPlayerTwo();

    void setPlayerTwo(Player player);

    void drawAll(Graphics g, ImageObserver observer);

    List<Alien> getAliens();
}
