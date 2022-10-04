package com.zetcode.sprite;

import lombok.Getter;
import walaniam.spaceinvaders.ImageResource;

import java.awt.Graphics;
import java.awt.image.ImageObserver;

@Getter
public class Alien extends Sprite {

    private final Bomb bomb;

    public Alien(int x, int y) {
        this.x = x;
        this.y = y;
        this.bomb = new Bomb(x, y);
        setImage(ImageResource.ALIEN);
    }

    public void act(int direction) {
        this.x += direction;
    }

    @Override
    public void draw(Graphics g, ImageObserver observer) {
        super.draw(g, observer);
        if (isDying()) {
            die();
        }
    }

    public class Bomb extends Sprite {

        public Bomb(int x, int y) {
            super(false);
            this.x = x;
            this.y = y;
            setImage(ImageResource.BOMB);
        }
    }
}
