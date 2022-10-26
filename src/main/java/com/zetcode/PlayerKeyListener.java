package com.zetcode;

import com.zetcode.sprite.Player;
import lombok.RequiredArgsConstructor;
import walaniam.spaceinvaders.model.GameModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@RequiredArgsConstructor
public class PlayerKeyListener extends KeyAdapter {

    private final AtomicReference<GameModel> modelRef;
    private final Function<GameModel, Player> playerFunction;

    private Player getPlayer() {
        return playerFunction.apply(modelRef.get());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        getPlayer().keyReleased(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        getPlayer().keyPressed(e);
    }
}
