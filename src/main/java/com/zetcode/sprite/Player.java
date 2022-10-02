package com.zetcode.sprite;

import com.zetcode.Commons;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 280;

    private final int width;

    public Player() {
        Image playerImage = ImageRepository.INSTANCE.getImage(ImageResource.PLAYER);
        this.width = playerImage.getWidth(null);
        setImage(playerImage);
        setX(START_X);
        setY(START_Y);
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
