package com.zetcode.sprite;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import walaniam.spaceinvaders.ImageResource;

@NoArgsConstructor
@EqualsAndHashCode
public class Bomb extends Sprite {

    public Bomb(int x, int y) {
        super(ImageResource.BOMB, false);
        this.x = x;
        this.y = y;
    }
}
