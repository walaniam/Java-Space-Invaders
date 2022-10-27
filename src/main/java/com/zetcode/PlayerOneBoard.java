package com.zetcode;

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
        log.trace("Player one pre sync...");
        remoteWrite.accept(modelRef.get());
    }

    @Override
    protected void postUpdateSync() {
        log.trace("Player one post sync...");
        if (multiplayerServer.isClientConnected()) {
            GameModel remoteModel = remoteRead.get(50, TimeUnit.MILLISECONDS);
            if (remoteModel != null) {
                modelRef.accumulateAndGet(remoteModel, (current, remote) -> {
                    current.mergeWith(remote);
                    return current;
                });
            } else {
                log.debug("Remote model was null");
            }
        }
    }
}
