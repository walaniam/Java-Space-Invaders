package com.zetcode.sprite;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public abstract class Sprite {

    @Setter(AccessLevel.PROTECTED)
    private boolean visible = true;
    private Image image;
    private boolean dying;

    protected int x;
    protected int y;
    protected int dx;

    public void die() {
        visible = false;
    }
}
