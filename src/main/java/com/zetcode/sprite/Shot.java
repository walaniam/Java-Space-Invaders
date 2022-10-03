package com.zetcode.sprite;

import com.zetcode.Commons;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;
import walaniam.spaceinvaders.model.GameState;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Shot extends Sprite {

    private static final int H_SPACE = 6;
    private static final int V_SPACE = 1;

    private final GameState state;
    private final List<Alien> aliens;
    private int fireRangeLeftX = 0;
    private int fireRangeRightX = Commons.ALIEN_WIDTH;
    private int fireRangeY = Commons.ALIEN_HEIGHT;

    static Shot regularShot(GameState state, List<Alien> aliens, int x, int y) {
        var shot = new Shot(state, aliens);
        shot.setImage(ImageRepository.INSTANCE.getImage(ImageResource.SHOT));
        shot.setX(x + H_SPACE);
        shot.setY(y - V_SPACE);
        return shot;
    }

    static Shot superShot(GameState state, List<Alien> aliens, int x, int y) {
        var shot = new Shot(state, aliens);
        shot.setImage(ImageRepository.INSTANCE.getImage(ImageResource.SUPER_SHOT));
        shot.setX(x + 2);
        shot.setY(y - 12);
        shot.fireRangeLeftX = Commons.ALIEN_WIDTH * 2;
        shot.fireRangeRightX = Commons.ALIEN_WIDTH * 2;
        return shot;
    }

    public void update() {

        if (!isVisible()) {
            return;
        }

        var hit = new AtomicBoolean();
        aliens.stream()
                .filter(alien -> alien.isVisible() && isVisible())
                .forEach(alien -> {
                    int alienX = alien.getX();
                    int alienY = alien.getY();
                    if (x >= (alienX - fireRangeLeftX) && x <= (alienX + fireRangeRightX)
                            && y >= alienY && y <= (alienY + fireRangeY)) {

                        Image explosionImg = ImageRepository.INSTANCE.getImage(ImageResource.EXPLOSION);
                        alien.setImage(explosionImg);
                        alien.setDying(true);
                        state.plusDeath();
                        hit.set(true);
                    }
                });

        if (hit.get()) {
            die();
        }

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
