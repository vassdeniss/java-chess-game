package com.chess.tests;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.player.MakeTransition;
import org.junit.jupiter.api.Test;

import static com.chess.engine.board.Move.MoveFactory;
import static org.junit.jupiter.api.Assertions.*;

class TestAi {
    @Test
    public void testFoolsMate() {
        final Board board = Board.createStandardBoard();
        final MakeTransition t1 = board.currentPlayer()
                .makeMove(MoveFactory.createMove(board, BoardUtils.getCoordinateAtPostiion("f2"),
                        BoardUtils.getCoordinateAtPostiion("f3")));
        assertTrue(t1.getMoveStatus().isDone());
        final MakeTransition t2 = t1.getTransitionBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t1.getTransitionBoard(), BoardUtils.getCoordinateAtPostiion("e7"),
                        BoardUtils.getCoordinateAtPostiion("e5")));

        assertTrue(t2.getMoveStatus().isDone());
        final MakeTransition t3 = t2.getTransitionBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t2.getTransitionBoard(), BoardUtils.getCoordinateAtPostiion("g2"),
                        BoardUtils.getCoordinateAtPostiion("g4")));

        assertTrue(t3.getMoveStatus().isDone());
        final MakeTransition t4 = t3.getTransitionBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t3.getTransitionBoard(), BoardUtils.getCoordinateAtPostiion("d8"),
                        BoardUtils.getCoordinateAtPostiion("h4")));

        assertTrue(t4.getMoveStatus().isDone());
        assertTrue(t4.getTransitionBoard().currentPlayer().isInCheckMate());
    }
}