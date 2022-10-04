package com.zetcode;

import lombok.extern.slf4j.Slf4j;

import javax.swing.JFrame;
import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class SpaceInvaders extends JFrame  {

    public SpaceInvaders(Optional<String> serverAddress) {

        serverAddress.ifPresentOrElse(
                remoteAddress -> {
                    add(Board.playerTwoBoard(remoteAddress));
                    setTitle("Space Invaders - " + remoteAddress);
                },
                () -> {
                    add(Board.playerOneBoard());
                    setTitle("Space Invaders");
                }
        );

        setSize(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        log.info("Starting game with params: {}", Arrays.stream(args).collect(Collectors.joining(", ")));
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
