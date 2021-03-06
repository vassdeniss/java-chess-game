package com.chess.tests;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Board.Builder;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move.MoveFactory;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.player.MakeTransition;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStaleMate {
    @Test
    public void testAnandKramnikStaleMate() {

        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(21, Alliance.BLACK));
        builder.setPiece(new King(36, Alliance.BLACK, false, false));
        // White Layout
        builder.setPiece(new Pawn(29, Alliance.WHITE));
        builder.setPiece(new King(31, Alliance.WHITE, false, false));
        builder.setPiece(new Pawn(39, Alliance.WHITE));
        // Set the current player
        builder.setMoveDecider(Alliance.BLACK);
        final Board board = builder.build();
        assertFalse(board.currentPlayer().isInStaleMate());
        final MakeTransition t1 = board.currentPlayer()
                .makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f5")));
        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getToBoard().currentPlayer().isInStaleMate());
        assertFalse(t1.getToBoard().currentPlayer().isInCheck());
        assertFalse(t1.getToBoard().currentPlayer().isInCheckMate());
    }

    @Test
    public void testAnonymousStaleMate() {
        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new King(2, Alliance.BLACK, false, false));
        // White Layout
        builder.setPiece(new Pawn(10, Alliance.WHITE));
        builder.setPiece(new King(26, Alliance.WHITE, false, false));
        // Set the current player
        builder.setMoveDecider(Alliance.WHITE);
        final Board board = builder.build();
        assertFalse(board.currentPlayer().isInStaleMate());
        final MakeTransition t1 = board.currentPlayer()
                .makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("c5"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c6")));
        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getToBoard().currentPlayer().isInStaleMate());
        assertFalse(t1.getToBoard().currentPlayer().isInCheck());
        assertFalse(t1.getToBoard().currentPlayer().isInCheckMate());
    }

    @Test
    public void testAnonymousStaleMate2() {
        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new King(0, Alliance.BLACK, false, false));
        // White Layout
        builder.setPiece(new Pawn(16, Alliance.WHITE));
        builder.setPiece(new King(17, Alliance.WHITE, false, false));
        builder.setPiece(new Bishop(19, Alliance.WHITE));
        // Set the current player
        builder.setMoveDecider(Alliance.WHITE);
        final Board board = builder.build();
        assertFalse(board.currentPlayer().isInStaleMate());
        final MakeTransition t1 = board.currentPlayer()
                .makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("a6"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("a7")));
        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getToBoard().currentPlayer().isInStaleMate());
        assertFalse(t1.getToBoard().currentPlayer().isInCheck());
        assertFalse(t1.getToBoard().currentPlayer().isInCheckMate());
    }
}