package com.chess.engine.player;

// Check if you can execute the move
public enum MoveStatus {
    DONE {
        @Override
        boolean isDone() { return true; }
    },
    ILLEGAL {
        @Override
        boolean isDone() { return false; }
    },
    CHECK {
        @Override
        boolean isDone() { return false; }
    };
    abstract boolean isDone();
}
