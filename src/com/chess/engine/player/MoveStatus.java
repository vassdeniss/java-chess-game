package com.chess.engine.player;

// Check if you can execute the move
public enum MoveStatus {
    DONE {
        @Override
        public boolean isDone() { return true; }
    },
    ILLEGAL {
        @Override
        public boolean isDone() { return false; }
    },
    CHECK {
        @Override
        public boolean isDone() { return false; }
    };
    public abstract boolean isDone();
}
