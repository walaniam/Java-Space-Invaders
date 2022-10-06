package com.zetcode;

import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.multi.BlockingExchange;
import walaniam.spaceinvaders.multi.StateExchangeClient;

@Slf4j
public class PlayerTwoBoard extends Board {

    private StateExchangeClient stateExchangeClient;

    public PlayerTwoBoard(GameModel gameModel,
                          BlockingExchange<GameModel> remoteModelExchange,
                          BlockingExchange<GameModel> localModelExchange) {
        super(gameModel, GameModel::getPlayerTwo, remoteModelExchange, localModelExchange);
    }

    public static PlayerTwoBoard connectToGame(String serverAddress) {

        var remoteRead = new BlockingExchange<GameModel>();
        var remoteWrite = new BlockingExchange<GameModel>();

        var client = new StateExchangeClient(
                serverAddress,
                remoteRead,
                remoteWrite
        );
        client.open();

        var remoteModel = remoteRead.get();

        var board = new PlayerTwoBoard(remoteModel, remoteRead, remoteWrite);
        board.stateExchangeClient = client;

        return board;
    }

    @Override
    protected void preUpdateSync() {
//        log.info("Player two pre sync...");
        var model = remoteRead.get();
        modelRef.set(model);
    }

    @Override
    protected void postUpdateSync() {
//        log.info("Player two post sync...");
        remoteWrite.accept(modelRef.get());
    }
}
