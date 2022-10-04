package com.zetcode;

import com.zetcode.sprite.Player;
import lombok.RequiredArgsConstructor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@RequiredArgsConstructor
public class PlayerKeyListener extends KeyAdapter {

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
