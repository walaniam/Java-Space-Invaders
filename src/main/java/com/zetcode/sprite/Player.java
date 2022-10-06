package com.zetcode.sprite;

import com.zetcode.Commons;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;
import walaniam.spaceinvaders.model.GameState;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.List;

@NoArgsConstructor
@Slf4j
@EqualsAndHashCode
@ToString(callSuper = true)
public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 280;

    private GameState state;
    @ToString.Exclude
    private List<Alien> aliens;
    private int width;
    private Shot shot;
    private Shot superShot;
    private int superShotsAvailable = 8;

    private Player(GameState state, List<Alien> aliens) {
        super(ImageResource.PLAYER);
        this.state = state;
        this.aliens = aliens;
        Image playerImage = ImageRepository.INSTANCE.getImage(getImage());
        this.width = playerImage.getWidth(null);
        this.x = START_X;
        this.y = START_Y;
    }

    public static Player playerOne(GameState state, List<Alien> aliens) {
        return new Player(state, aliens);
    }

    public static Player playerTwo(GameState state, List<Alien> aliens) {
        var player = new Player(state, aliens);
        player.setImage(ImageResource.PLAYER_TWO);
        player.x = START_X + 25;
        return player;
    }

    @Override
    public void draw(Graphics g, ImageObserver observer) {
        super.draw(g, observer);
        if (isDying()) {
            log.info("Player dying");
            die();
            state.setInGame(false);
        }
        if (shot != null) {
            shot.draw(g, observer);
        }
        if (superShot != null) {
            superShot.draw(g, observer);
        }
    }

    @Override
    public void update() {
        if (shot != null) {
            shot.update();
        }
        if (superShot != null) {
            superShot.update();
        }
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
//        log.info("Key pressed {}", e);
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT -> dx = -2;
            case KeyEvent.VK_RIGHT -> dx = 2;
            case KeyEvent.VK_B -> superShotFired();
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
        if (state.isInGame() && (shot == null || !shot.isVisible())) {
            shot = Shot.regularShot(state, aliens, x, y);
        }
    }

    private void superShotFired() {
        if (state.isInGame() && (superShot == null || !superShot.isVisible() && superShotsAvailable > 0)) {
            superShotsAvailable--;
            superShot = Shot.superShot(state, aliens, x, y);
        }
    }
}
