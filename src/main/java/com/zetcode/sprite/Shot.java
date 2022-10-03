package com.zetcode.sprite;

import com.zetcode.Commons;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;
import walaniam.spaceinvaders.model.GameState;

import java.awt.*;
import java.util.List;

import static java.util.Collections.emptyList;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Shot extends Sprite {

    private static final int H_SPACE = 6;
    private static final int V_SPACE = 1;

    private GameState state;
    private List<Alien> aliens = emptyList();

    static Shot ofPlayerPosition(GameState state, List<Alien> aliens, int x, int y) {
        var shot = new Shot();
        shot.setImage(ImageRepository.INSTANCE.getImage(ImageResource.SHOT));
        shot.setX(x + H_SPACE);
        shot.setY(y - V_SPACE);
        shot.state = state;
        shot.aliens = aliens;
        return shot;
    }

    public void update() {

        if (!isVisible()) {
            return;
        }

        aliens.stream()
                .filter(alien -> alien.isVisible() && isVisible())
                .forEach(alien -> {
                    int alienX = alien.getX();
                    int alienY = alien.getY();
                    if (x >= alienX
                            && x <= (alienX + Commons.ALIEN_WIDTH)
                            && y >= alienY
                            && y <= (alienY + Commons.ALIEN_HEIGHT)) {

                        Image explosionImg = ImageRepository.INSTANCE.getImage(ImageResource.EXPLOSION);
                        alien.setImage(explosionImg);
                        alien.setDying(true);
                        state.plusDeath();
                        die();
                    }
                });

        updatePosition();
    }

    protected void updatePosition() {
        int newY = y - 4;
        if (newY < 0) {
            die();
        } else {
            y = newY;
        }
    }
}
