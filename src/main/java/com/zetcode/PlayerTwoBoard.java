package com.zetcode;

import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameModelImpl;
import walaniam.spaceinvaders.multi.MultiplayerClient;
import walaniam.spaceinvaders.multi.MultiplayerContext;

@Slf4j
public class PlayerTwoBoard extends Board {

    private MultiplayerClient multiplayerClient;

    protected PlayerTwoBoard(MultiplayerContext context) {
        super(GameModel::getPlayerTwo, context);
    }

    public static PlayerTwoBoard connectToGame(String serverAddress) {

        var playerContext = new MultiplayerContext();

        var client = new MultiplayerClient(serverAddress, playerContext);
        client.open();

        var remoteModel = playerContext.getRemoteRead().get();
        playerContext.getModelRef().set(remoteModel);

        var board = new PlayerTwoBoard(playerContext);
        board.multiplayerClient = client;

        return board;
    }

    @Override
    protected void preUpdateSync() {
        log.debug("Player two pre sync...");
        GameModel remoteModel = remoteRead.get();
        modelRef.accumulateAndGet(remoteModel, (current, update) -> {
            GameModelImpl remoteModelImpl = (GameModelImpl) update;
            remoteModelImpl.setPlayerTwo(current.getPlayerTwo());
            return remoteModelImpl;
        });
    }

    @Override
    protected void postUpdateSync() {
        log.debug("Player two post sync...");
        remoteWrite.accept(modelRef.get());
    }
}
