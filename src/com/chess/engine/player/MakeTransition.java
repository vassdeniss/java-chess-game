package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public class MakeTransition {
    // Class for transitioning between boards when making a move
    // Fields to save over the transition
    private final Board fromBoard;
    private final Board toBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MakeTransition(final Board fromBoard,
                          final Board toBoard,
                          final Move move,
                          final MoveStatus moveStatus) {
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public Board getFromBoard() { return this.fromBoard; }
    public Board getToBoard() { return this.toBoard; }
    public Move getTransitionMove() { return this.move; }
    public MoveStatus getMoveStatus() { return this.moveStatus; }
}