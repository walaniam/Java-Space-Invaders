package com.zetcode;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class SpaceInvaders extends JFrame  {

    public SpaceInvaders() {

        add(new Board());

        setTitle("Space Invaders");
        setSize(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        log.info("Starting game with params: {}", Arrays.stream(args).collect(Collectors.joining(", ")));
        EventQueue.invokeLater(() -> {
            var frame = new SpaceInvaders();
            frame.setVisible(true);
        });
    }
}
