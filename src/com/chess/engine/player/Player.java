package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

import java.util.Collection;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;

    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = legalMoves;
    }

    // Method for looping through the active pieces
    // if a king is found return it
    // if not throw a runtime exception
    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Not a valid board!");
    }

    // Method for checking is a move is contained in
    // the collection of moves
    // STUB
    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    // Method for checking if you're in check
    // STUB
    public boolean isInCheck() {
        return false;
    }

    // Method for checking if you're in checkmate
    // STUB
    public boolean isInCheckMate() {
        return false;
    }

    // Method for checking if you're in stalemate
    // STUB
    public boolean isInStaleMate() {
        return false;
    }

    // Method for checking if you're castled
    // STUB
    public boolean isCastled() {
        return false;
    }

    public MakeTransition makeMove(final Move move) {
        return null;

    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
}