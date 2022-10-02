package com.zetcode;

import com.zetcode.sprite.Alien;
import lombok.RequiredArgsConstructor;
import walaniam.spaceinvaders.GameModel;
import walaniam.spaceinvaders.ImageRepository;
import walaniam.spaceinvaders.ImageResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Random;

public class Board extends JPanel {

    private final GameModel model;
    private Dimension dimension;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private Timer timer;

    public Board() {
        this.model = new GameModel();
        initBoard(model);
    }

    private void initBoard(GameModel model) {

        addKeyListener(new TAdapter(model));
        setFocusable(true);
        dimension = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
        setBackground(Color.black);

        timer = new Timer(Commons.DELAY, new GameCycle());
        timer.start();
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

        if (inGame) {
            g.drawLine(0, Commons.GROUND, Commons.BOARD_WIDTH, Commons.GROUND);
            inGame = model.drawAll(g, this);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
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

        if (deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        var player = model.getPlayer();
        var aliens = model.getAliens();

        // player
        player.act();

        // shot
        updateShot();

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

        Iterator<Alien> it = aliens.iterator();

        while (it.hasNext()) {

            Alien alien = it.next();

            if (alien.isVisible()) {

                int y = alien.getY();

                if (y > Commons.GROUND - Commons.ALIEN_HEIGHT) {
                    inGame = false;
                    message = "Invasion!";
                }

                alien.act(direction);
            }
        }

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
                    bomb.setVisible(false);
                }
            }

            if (bomb.isVisible()) {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= Commons.GROUND - Commons.BOMB_HEIGHT) {
                    bomb.setVisible(false);
                }
            }
        }
    }

    private void updateShot() {

        var shot = model.getShot();
        var aliens = model.getAliens();

        if (!shot.isVisible()) {
            return;
        }

        int shotX = shot.getX();
        int shotY = shot.getY();

        aliens.stream()
                .filter(alien -> alien.isVisible() && shot.isVisible())
                .forEach(alien -> {
                    int alienX = alien.getX();
                    int alienY = alien.getY();
                    if (shotX >= alienX
                            && shotX <= (alienX + Commons.ALIEN_WIDTH)
                            && shotY >= alienY
                            && shotY <= (alienY + Commons.ALIEN_HEIGHT)) {

                        Image explosionImg = ImageRepository.INSTANCE.getImage(ImageResource.EXPLOSION);
                        alien.setImage(explosionImg);
                        alien.setDying(true);
                        deaths++;
                        shot.die();
                    }
                });

        int y = shot.getY();
        y -= 4;

        if (y < 0) {
            shot.die();
        } else {
            shot.setY(y);
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
    private class TAdapter extends KeyAdapter {

        private final GameModel model;

        @Override
        public void keyReleased(KeyEvent e) {
            model.getPlayer().keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            var player = model.getPlayer();
            player.keyPressed(e);
            if (key == KeyEvent.VK_SPACE && inGame) {
                model.shotFired();
            }
        }
    }
}
