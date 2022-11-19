package com.zetcode.sprite;

import com.zetcode.Commons;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import walaniam.spaceinvaders.ImageResource;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameState;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor
@EqualsAndHashCode
public class Shot extends Sprite {

    private static final int H_SPACE = 6;
    private static final int V_SPACE = 1;

    private GameState state;
    private int fireRangeLeftX = 0;
    private int fireRangeRightX = Commons.ALIEN_WIDTH;
    private final int fireRangeY = Commons.ALIEN_HEIGHT;

    private Shot(ImageResource image, GameState state) {
        super(image);
        this.state = state;
    }

    static Shot regularShot(GameState state, int x, int y) {
        var shot = new Shot(ImageResource.SHOT, state);
        shot.setX(x + H_SPACE);
        shot.setY(y - V_SPACE);
        return shot;
    }

    static Shot superShot(GameState state, int x, int y) {
        var shot = new Shot(ImageResource.SUPER_SHOT, state);
        shot.setImage(ImageResource.SUPER_SHOT);
        shot.setX(x + 2);
        shot.setY(y - 12);
        shot.fireRangeLeftX = (int) Math.round(Commons.ALIEN_WIDTH * 2.5);
        shot.fireRangeRightX = (int) Math.round(Commons.ALIEN_WIDTH * 2.5);
        return shot;
    }

    @Override
    public void update(GameModel model) {

        if (!isVisible()) {
            return;
        }

        List<Alien> aliens = model.getAliens();
        var hit = new AtomicBoolean();
        aliens.stream()
                .filter(alien -> alien.isVisible() && isVisible())
                .forEach(alien -> {
                    int alienX = alien.getX();
                    int alienY = alien.getY();
                    if (x >= (alienX - fireRangeLeftX) && x <= (alienX + fireRangeRightX)
                            && y >= alienY && y <= (alienY + fireRangeY)) {

                        alien.setImage(ImageResource.EXPLOSION);
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
