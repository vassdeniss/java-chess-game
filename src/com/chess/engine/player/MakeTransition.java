package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public class MakeTransition {
    // Class for transitioning between boards when making a move
    // Fields to save over the transition
    // STUB
    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MakeTransition(final Board transitionBoard,
                          final Move move,
                          final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }
}