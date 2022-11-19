package com.zetcode;

import lombok.extern.slf4j.Slf4j;

import javax.swing.JFrame;
import java.awt.EventQueue;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class SpaceInvaders extends JFrame  {

    public SpaceInvaders(Optional<String> serverAddress) {

        final AtomicReference<Board> boardRef = new AtomicReference<>();
        serverAddress.ifPresentOrElse(
                remoteAddress -> {
                    var board = PlayerTwoBoard.connectToGame(remoteAddress);
                    boardRef.set(board);
                    setTitle("Space Invaders - " + remoteAddress);
                },
                () -> {
                    var board = PlayerOneBoard.multiPlayerBoard();
                    boardRef.set(board);
                    setTitle("Space Invaders");
                }
        );

        add(boardRef.get());

        setSize(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        boardRef.get().startGame();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        log.info("Starting game with params: {}", String.join(",", args));
        EventQueue.invokeLater(() -> {
            String gameServerAddress = null;
            if (args.length > 0) {
                gameServerAddress = args[0];
            }
            var frame = new SpaceInvaders(Optional.ofNullable(gameServerAddress));
            frame.setVisible(true);
        });
    }
}
