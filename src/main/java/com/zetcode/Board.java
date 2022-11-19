package com.zetcode;

import com.zetcode.sprite.*;
import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.ImageResource;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameState;
import walaniam.spaceinvaders.multi.BlockingExchange;
import walaniam.spaceinvaders.multi.MultiplayerContext;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Slf4j
public abstract class Board extends JPanel {

    protected String gameEndMessage = "Game Over";

    private final Dimension dimension = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
    protected final BlockingExchange<GameModel> remoteRead;
    protected final BlockingExchange<GameModel> remoteWrite;
    protected final AtomicReference<GameModel> modelRef;
    private final Function<GameModel, Player> playerFunction;
    private final Timer timer;

    protected Board(Function<GameModel, Player> playerFunction,
                    MultiplayerContext multiplayerContext) {
        this.modelRef = multiplayerContext.getModelRef();
        this.playerFunction = playerFunction;
        this.remoteRead = multiplayerContext.getRemoteRead();
        this.remoteWrite = multiplayerContext.getRemoteWrite();
        addKeyListener(new PlayerKeyListener(modelRef, playerFunction));
        setFocusable(true);
        setBackground(Color.black);
        this.timer = new Timer(Commons.DELAY, new GameCycle());
    }

    public void startGame() {
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        var model = modelRef.get();

        g.setColor(Color.black);
        g.fillRect(0, 0, dimension.width, dimension.height);
        g.setColor(Color.green);

        if (model.isInGame()) {
            g.drawLine(0, Commons.GROUND, Commons.BOARD_WIDTH, Commons.GROUND);
            model.drawAll(g, this);
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
        g.drawString(gameEndMessage, (Commons.BOARD_WIDTH - fontMetrics.stringWidth(gameEndMessage)) / 2,
                Commons.BOARD_WIDTH / 2);
    }

    private void updateModel() {

        GameModel model = modelRef.get();
        Player player = playerFunction.apply(model);

        if (model.getDeaths() == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {
            model.gameEnd(GameState.GameEndCause.WIN);
            timer.stop();
            gameEndMessage = "Game won!";
        } else {
            // TODO
//            System.out.println(Commons.NUMBER_OF_ALIENS_TO_DESTROY + ", deaths " + model.getDeaths());
        }

        List<Alien> aliens = model.getAliens();

        // player
        player.act();

        // shot
        player.update(model);

        // aliens

        for (Alien alien : aliens) {

            int x = alien.getX();

            if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT && model.getAlienDirection() != Direction.LEFT) {

                model.setAlienDirection(Direction.LEFT);

                for (Alien a2 : aliens) {
                    a2.setY(a2.getY() + Commons.GO_DOWN);
                }
            }

            if (x <= Commons.BORDER_LEFT && model.getAlienDirection() != Direction.RIGHT) {

                model.setAlienDirection(Direction.RIGHT);

                for (Alien a : aliens) {
                    a.setY(a.getY() + Commons.GO_DOWN);
                }
            }
        }

        aliens.stream()
                .filter(Sprite::isVisible)
                .forEach(alien -> {
                    int y = alien.getY();
                    if (y > Commons.GROUND - Commons.ALIEN_HEIGHT) {
                        model.gameEnd(GameState.GameEndCause.INVASION);
                        gameEndMessage = "Invasion!";
                    }
                    alien.act(model.getAlienDirection());
                });

        // bombs
        var generator = new Random();

        for (Alien alien : aliens) {

            int shot = generator.nextInt(15);
            Bomb bomb = alien.getBomb();

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

                    if (!player.isImmortal()) {
                        player.setImage(ImageResource.EXPLOSION);
                        player.setDying(true);
                    }

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

    protected abstract void preUpdateSync();

    protected abstract void postUpdateSync();

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            preUpdateSync();
            updateModel();
            postUpdateSync();
            Board.this.repaint();
        }
    }

}
