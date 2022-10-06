package com.zetcode.sprite;

import lombok.*;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;

import java.awt.Graphics;
import java.awt.image.ImageObserver;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class Sprite {

    private ImageResource image;
    private boolean visible;
    private boolean dying;

    protected int x;
    protected int y;
    protected int dx;

    public Sprite(ImageResource image) {
        this(image, true);
    }

    public Sprite(ImageResource image, boolean visible) {
        this.image = image;
        this.visible = visible;
    }

    public void die() {
        visible = false;
    }

    public void draw(Graphics g, ImageObserver observer) {
        if (visible) {
            var awtImage = ImageRepository.INSTANCE.getImage(image);
            g.drawImage(awtImage, x, y, observer);
        }
    }

    public void update() {
    }
}
