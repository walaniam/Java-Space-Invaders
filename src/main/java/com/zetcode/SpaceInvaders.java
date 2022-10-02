package com.zetcode;

import javax.swing.*;
import java.awt.*;

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
        EventQueue.invokeLater(() -> {
            var frame = new SpaceInvaders();
            frame.setVisible(true);
        });
    }
}
