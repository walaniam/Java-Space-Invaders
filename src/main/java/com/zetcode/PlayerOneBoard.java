package com.zetcode;

import com.zetcode.sprite.Player;
import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameModelImpl;
import walaniam.spaceinvaders.multi.MultiplayerContext;
import walaniam.spaceinvaders.multi.MultiplayerServer;

import java.util.concurrent.TimeUnit;

@Slf4j
public class PlayerOneBoard extends Board {

    private MultiplayerServer multiplayerServer;

    protected PlayerOneBoard(MultiplayerContext multiplayerContext) {
        super(GameModel::getPlayer, multiplayerContext);
    }

    public static PlayerOneBoard multiPlayerBoard() {

        var model = new GameModelImpl();
        var multiplayerContext = new MultiplayerContext(model);

        multiplayerContext.getRemoteWrite().accept(model);

        var stateExchangeServer = new MultiplayerServer(multiplayerContext);
        stateExchangeServer.open();

        var board = new PlayerOneBoard(multiplayerContext);
        board.multiplayerServer = stateExchangeServer;

        return board;
    }

    @Override
    protected void preUpdateSync() {
        log.debug("Player one pre sync...");
        remoteWrite.accept(modelRef.get());
    }

    @Override
    protected void postUpdateSync() {
        log.debug("Player one post sync...");
        if (multiplayerServer.isClientConnected()) {
            GameModel remoteModel = remoteRead.get(2, TimeUnit.SECONDS);
            if (remoteModel != null) {
                modelRef.accumulateAndGet(remoteModel, (current, remote) -> {
                    Player playerTwo = remote.getPlayerTwo();
                    log.debug("Player two: {}", playerTwo);
                    current.setPlayerTwo(playerTwo);
                    return current;
                });
            } else {
                log.info("Remote model was null");
            }
        }
    }
}
