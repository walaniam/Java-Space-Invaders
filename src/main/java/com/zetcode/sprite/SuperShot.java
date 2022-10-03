package com.zetcode.sprite;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;
import walaniam.spaceinvaders.model.GameState;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SuperShot extends Sprite {

    private static final int H_SPACE = 12;
    private static final int V_SPACE = 12;

    static SuperShot ofPlayerPosition(GameState state, List<Alien> aliens, int x, int y) {
        var shot = new SuperShot();
        shot.setImage(ImageRepository.INSTANCE.getImage(ImageResource.SUPER_SHOT));
        shot.setX(x + H_SPACE);
        shot.setY(y - V_SPACE);
        return shot;
    }
}
