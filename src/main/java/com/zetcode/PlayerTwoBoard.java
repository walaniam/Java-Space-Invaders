package com.zetcode;

import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameModelImpl;
import walaniam.spaceinvaders.multi.MultiplayerClient;
import walaniam.spaceinvaders.multi.MultiplayerContext;

import java.util.concurrent.TimeUnit;

@Slf4j
public class PlayerTwoBoard extends Board {

    private MultiplayerClient multiplayerClient;

    protected PlayerTwoBoard(MultiplayerContext context) {
        super(GameModel::getPlayerTwo, context);
    }

    public static PlayerTwoBoard connectToGame(String serverAddress) {

        log.info("Connecting to {}", serverAddress);

        var playerContext = new MultiplayerContext();

        var client = new MultiplayerClient(serverAddress, playerContext);
        client.open();

        var remoteModel = playerContext.getRemoteRead().get();
        remoteModel.getPlayerTwo().setImmortal(true);
        playerContext.getModelRef().set(remoteModel);

        var board = new PlayerTwoBoard(playerContext);
        board.multiplayerClient = client;

        log.info("Player two board initialized with model: {}", remoteModel);

        return board;
    }

    @Override
    protected void preUpdateSync() {
        log.trace("Player two pre sync...");
        GameModel remoteModel = remoteRead.get(50, TimeUnit.MILLISECONDS);
        if (remoteModel != null) {
            modelRef.accumulateAndGet(remoteModel, (current, update) -> {
                GameModelImpl remoteModelImpl = (GameModelImpl) update;
                remoteModelImpl.setPlayerTwo(current.getPlayerTwo());
                return remoteModelImpl;
            });
        } else {
            log.info("Remote model was null");
        }
    }

    @Override
    protected void postUpdateSync() {
        log.trace("Player two post sync...");
        if (modelRef.get() == null) {
            throw new IllegalArgumentException();
        }
        remoteWrite.accept(modelRef.get());
    }
}
