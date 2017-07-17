package com.tba.movingvehicles.model;

import java.awt.event.KeyEvent;

public enum Direction {
    UP("UP"), DOWN("DOWN"), LEFT("LEFT"), RIGHT("RIGHT");

    private String key;

    private Direction(String key) {
        this.key = key;
    }

    private Direction(){}

    public String getKey() {
        return key;
    }
}
