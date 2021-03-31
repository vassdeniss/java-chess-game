package com.chess.engine.player;

// Check if you can execute the move
// STUB
public enum MoveStatus {
    DONE {
        @Override
        boolean isDone() {
            return true;
        }
    };

    abstract boolean isDone();
}
