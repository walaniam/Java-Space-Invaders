package com.zetcode.sprite;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Shot extends Sprite {

    private static final int H_SPACE = 6;
    private static final int V_SPACE = 1;

    static Shot nonVisible() {
        var shot = new Shot();
        shot.setVisible(false);
        return shot;
    }

    static Shot ofPlayerPosition(int x, int y) {
        var shot = new Shot();
        shot.setImage(ImageRepository.INSTANCE.getImage(ImageResource.SHOT));
        shot.setX(x + H_SPACE);
        shot.setY(y - V_SPACE);
        return shot;
    }
}
