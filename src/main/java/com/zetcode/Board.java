package com.zetcode;

import com.zetcode.sprite.Alien;
import com.zetcode.sprite.Player;
import com.zetcode.sprite.Sprite;
import lombok.RequiredArgsConstructor;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;
import walaniam.spaceinvaders.model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Random;

public class Board extends JPanel {

    private final GameModel model = new GameModel();
    private final Dimension dimension = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
    private final Timer timer;

    private int direction = -1;
    private String message = "Game Over";

    public Board() {
        addKeyListener(new PlayerKeyListener(model.getPlayer()));
        setFocusable(true);
        setBackground(Color.black);

        this.timer = new Timer(Commons.DELAY, new GameCycle());
        this.timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, dimension.width, dimension.height);
        g.setColor(Color.green);

        if (model.isInGame()) {
            g.drawLine(0, Commons.GROUND, Commons.BOARD_WIDTH, Commons.GROUND);
            model.drawAll(g, this);
            printState(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void printState(Graphics g) {
        // TODO
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (Commons.BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                Commons.BOARD_WIDTH / 2);
    }

    private void update() {

        if (model.getDeaths() == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {
            model.setInGame(false);
            timer.stop();
            message = "Game won!";
        }

        var player = model.getPlayer();
        var aliens = model.getAliens();

        // player
        player.act();

        // shot
        player.update();

        // aliens

        for (Alien alien : aliens) {

            int x = alien.getX();

            if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT && direction != -1) {

                direction = -1;

                Iterator<Alien> i1 = aliens.iterator();
                while (i1.hasNext()) {
                    Alien a2 = i1.next();
                    a2.setY(a2.getY() + Commons.GO_DOWN);
                }
            }

            if (x <= Commons.BORDER_LEFT && direction != 1) {

                direction = 1;

                Iterator<Alien> i2 = aliens.iterator();
                while (i2.hasNext()) {
                    Alien a = i2.next();
                    a.setY(a.getY() + Commons.GO_DOWN);
                }
            }
        }

        aliens.stream()
                .filter(Sprite::isVisible)
                .forEach(alien -> {
                    int y = alien.getY();
                    if (y > Commons.GROUND - Commons.ALIEN_HEIGHT) {
                        model.setInGame(false);
                        message = "Invasion!";
                    }
                    alien.act(direction);
                });

        // bombs
        var generator = new Random();

        for (Alien alien : aliens) {

            int shot = generator.nextInt(15);
            Alien.Bomb bomb = alien.getBomb();

            if (shot == Commons.CHANCE && alien.isVisible() && !bomb.isVisible()) {
                bomb.setVisible(true);
                bomb.setX(alien.getX());
                bomb.setY(alien.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && bomb.isVisible()) {

                if (bombX >= (playerX)
                        && bombX <= (playerX + Commons.PLAYER_WIDTH)
                        && bombY >= (playerY)
                        && bombY <= (playerY + Commons.PLAYER_HEIGHT)) {

                    player.setImage(ImageRepository.INSTANCE.getImage(ImageResource.EXPLOSION));
                    player.setDying(true);
                    bomb.die();
                }
            }

            if (bomb.isVisible()) {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= Commons.GROUND - Commons.BOMB_HEIGHT) {
                    bomb.die();
                }
            }
        }
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            update();
            Board.this.repaint();
        }
    }

    @RequiredArgsConstructor
    private class PlayerKeyListener extends KeyAdapter {

        private final Player player;

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
        }
    }
}
