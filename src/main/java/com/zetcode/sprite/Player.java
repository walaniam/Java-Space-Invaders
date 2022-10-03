package com.zetcode.sprite;

import com.zetcode.Commons;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.GameState;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

@Slf4j
public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 280;

    private final GameState state;
    private final int width;
    @Getter
    private Shot shot;

    public Player(GameState state) {
        this.state = state;
        Image playerImage = ImageRepository.INSTANCE.getImage(ImageResource.PLAYER);
        this.width = playerImage.getWidth(null);
        setImage(playerImage);
        setX(START_X);
        setY(START_Y);
        this.shot = new Shot();
    }

    @Override
    public void draw(Graphics g, ImageObserver observer) {
        super.draw(g, observer);
        if (isDying()) {
            log.info("Player dying");
            die();
            state.setInGame(false);
        }
        shot.draw(g, observer);
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
        switch (key) {
            case KeyEvent.VK_LEFT -> dx = -2;
            case KeyEvent.VK_RIGHT -> dx = 2;
            case KeyEvent.VK_SPACE -> shotFired();
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> dx = 0;
        }
    }

    private void shotFired() {
        if (!shot.isVisible() && state.isInGame()) {
            shot = new Shot(x, y);
        }
    }
}
