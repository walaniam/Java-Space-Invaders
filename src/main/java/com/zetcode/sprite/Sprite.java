package com.zetcode.sprite;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.image.ImageObserver;

@Getter
@Setter
public abstract class Sprite {

    private boolean visible;
    private Image image;
    private boolean dying;

    protected int x;
    protected int y;
    protected int dx;

    public Sprite() {
        this(true);
    }

    public Sprite(boolean visible) {
        this.visible = visible;
    }

    public void die() {
        visible = false;
    }

    public void draw(Graphics g, ImageObserver observer) {
        if (visible) {
            g.drawImage(image, x, y, observer);
        }
    }

    public void update() {
    }
}
