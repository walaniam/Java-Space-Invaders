package com.zetcode.sprite;

import com.zetcode.Commons;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameState;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

@NoArgsConstructor
@Slf4j
@EqualsAndHashCode
@ToString(callSuper = true)
public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 280;

    private GameState state;
    @Getter
    private PlayerDescriptor descriptor;
    private int width;
    private Shot shot;
    private Shot superShot;
    private int superShotsAvailable = 5;
    @Getter @Setter
    private boolean immortal;

    private Player(GameState state, PlayerDescriptor descriptor) {
        super(ImageResource.PLAYER);
        this.state = state;
        Image playerImage = ImageRepository.INSTANCE.getImage(getImage());
        this.width = playerImage.getWidth(null);
        this.x = START_X;
        this.y = START_Y;
        this.descriptor = descriptor;
        state.getPlayers().add(descriptor);
    }

    public static Player playerOne(GameState state) {
        return new Player(state, PlayerDescriptor.ONE);
    }

    public static Player playerTwo(GameState state) {
        var player = new Player(state, PlayerDescriptor.TWO);
        player.setImage(ImageResource.PLAYER_TWO);
        player.x = START_X + 25;
        return player;
    }

    @Override
    public void draw(Graphics g, ImageObserver observer) {
        super.draw(g, observer);
        if (isVisible() && isDying()) {
            log.info("Player {} dying", descriptor);
            die();
        }
        if (shot != null) {
            shot.draw(g, observer);
        }
        if (superShot != null) {
            superShot.draw(g, observer);
        }
    }

    @Override
    public void die() {
        super.die();
        state.getPlayers().remove(descriptor);
    }

    @Override
    public void update(GameModel model) {
        if (shot != null) {
            shot.update(model);
        }
        if (superShot != null) {
            superShot.update(model);
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
        log.debug("Key pressed {}", e);
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
            shot = Shot.regularShot(state, x, y);
        }
    }

    private void superShotFired() {
        if (state.isInGame() && (superShot == null || !superShot.isVisible() && superShotsAvailable > 0)) {
            superShotsAvailable--;
            superShot = Shot.superShot(state, x, y);
        }
    }
}
