package com.zetcode.sprite;

import lombok.Getter;
import lombok.Setter;
import walaniam.spaceinvaders.ImageResource;

import static walaniam.spaceinvaders.ImageUtils.loadImage;

public class Alien extends Sprite {
    @Getter
    private final Bomb bomb;

    public Alien(int x, int y) {
        this.x = x;
        this.y = y;
        bomb = new Bomb(x, y);
        setImage(loadImage(ImageResource.ALIEN));
    }

    public void act(int direction) {
        this.x += direction;
    }

    @Getter
    @Setter
    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {
            this.destroyed = true;
            this.x = x;
            this.y = y;
            setImage(loadImage(ImageResource.BOMB));
        }
    }
}
