package com.zetcode;

public interface Commons {

    int BOARD_WIDTH = 358;
    int BOARD_HEIGHT = 350;
    int BORDER_RIGHT = 30;
    int BORDER_LEFT = 5;

    int GROUND = 290;
    int BOMB_HEIGHT = 5;

    int ALIEN_HEIGHT = 12;
    int ALIEN_WIDTH = 12;
    int ALIEN_INIT_X = 150;
    int ALIEN_INIT_Y = 5;

    int GO_DOWN = 15;
    int ALIENS_ROWS = 4;
    int ALIENS_COLUMNS = 6;
    int NUMBER_OF_ALIENS_TO_DESTROY = ALIENS_ROWS * ALIENS_COLUMNS;
    int CHANCE = 5;
    int DELAY = 17; // * 20;
    int PLAYER_WIDTH = 15;
    int PLAYER_HEIGHT = 10;
}
