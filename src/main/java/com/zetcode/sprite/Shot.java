package com.zetcode.sprite;

import walaniam.spaceinvaders.ImageResource;

import static walaniam.spaceinvaders.ImageUtils.loadImage;

public class Shot extends Sprite {

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {

        setImage(loadImage(ImageResource.SHOT));

        int H_SPACE = 6;
        setX(x + H_SPACE);

        int V_SPACE = 1;
        setY(y - V_SPACE);
    }
}
