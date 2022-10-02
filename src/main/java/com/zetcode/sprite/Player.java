package com.zetcode.sprite;

import com.zetcode.Commons;
import walaniam.spaceinvaders.ImageResource;

import java.awt.*;
import java.awt.event.KeyEvent;

import static walaniam.spaceinvaders.ImageUtils.loadImage;

public class Player extends Sprite {

    private final int width;

    public Player() {
        Image playerImage = loadImage(ImageResource.PLAYER);
        width = playerImage.getWidth(null);
        setImage(playerImage);

        int startX = 270;
        setX(startX);

        int startY = 280;
        setY(startY);
    }

    public void act() {

        x += dx;

        if (x <= 2) {
            x = 2;
        }

        if (x >= Commons.BOARD_WIDTH - 2 * width) {
            x = Commons.BOARD_WIDTH - 2 * width;
        }
    }

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -2;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 2;
        }
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }
}
