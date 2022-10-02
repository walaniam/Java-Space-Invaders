package com.zetcode.sprite;

import lombok.NoArgsConstructor;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;

@NoArgsConstructor
public class Shot extends Sprite {

    private static final int H_SPACE = 6;
    private static final int V_SPACE = 1;

    public Shot(int x, int y) {
        setImage(ImageRepository.INSTANCE.getImage(ImageResource.SHOT));
        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }
}
