package com.zetcode.sprite;

import lombok.Getter;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;

@Getter
public class Alien extends Sprite {

    private final Bomb bomb;

    public Alien(int x, int y) {
        this.x = x;
        this.y = y;
        this.bomb = new Bomb(x, y);
        setImage(ImageRepository.INSTANCE.getImage(ImageResource.ALIEN));
    }

    public void act(int direction) {
        this.x += direction;
    }

    public class Bomb extends Sprite {

        public Bomb(int x, int y) {
            super(false);
            this.x = x;
            this.y = y;
            setImage(ImageRepository.INSTANCE.getImage(ImageResource.BOMB));
        }
    }
}
