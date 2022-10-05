package com.zetcode;

import lombok.extern.slf4j.Slf4j;
import walaniam.spaceinvaders.model.GameModel;
import walaniam.spaceinvaders.model.GameModelImpl;
import walaniam.spaceinvaders.multi.BlockingExchange;
import walaniam.spaceinvaders.multi.StateExchangeServer;

@Slf4j
public class PlayerOneBoard extends Board {

    private StateExchangeServer stateExchangeServer;

    public PlayerOneBoard(GameModel gameModel,
                          BlockingExchange<GameModel> remoteModelExchange,
                          BlockingExchange<GameModel> localModelExchange) {
        super(gameModel, GameModel::getPlayer, remoteModelExchange, localModelExchange);
    }

    public static PlayerOneBoard multiPlayerBoard() {

        var model = new GameModelImpl();

        var remoteRead = new BlockingExchange<GameModel>();
        var remoteWrite = new BlockingExchange<GameModel>();
        remoteWrite.accept(model);

        var stateExchangeServer = new StateExchangeServer(remoteRead, remoteWrite);
        stateExchangeServer.open();

        var board = new PlayerOneBoard(model, remoteRead, remoteWrite);
        board.stateExchangeServer = stateExchangeServer;

        return board;
    }

    @Override
    protected void syncGameModels() {
        log.debug("Player one sync...");
        remoteWrite.accept(modelRef.get());
    }
}
