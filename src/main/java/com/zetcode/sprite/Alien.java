package com.zetcode.sprite;

import lombok.Getter;
import walaniam.spaceinvaders.ImageResource;

import java.awt.Graphics;
import java.awt.image.ImageObserver;

@Getter
public class Alien extends Sprite {

    private final Bomb bomb;

    public Alien(int x, int y) {
        super(ImageResource.ALIEN);
        this.x = x;
        this.y = y;
        this.bomb = new Bomb(x, y);
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
            super(ImageResource.BOMB, false);
            this.x = x;
            this.y = y;
        }
    }
}
