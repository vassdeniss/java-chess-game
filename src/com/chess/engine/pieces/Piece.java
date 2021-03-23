package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {
    // Set int for the position of the piece on the board
    // Set an "alliance" i.e the piece/player color
    // Set boolean to check for first move
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;

    // Constructor
    Piece(final int piecePosition, final Alliance pieceAlliance) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = false;
    }

    // Method for taking piece position
    public int getPiecePosition() {
        return this.piecePosition;
    }

    // Method for taking the pieces color
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    // Method for returning first move boolean
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    // Abstract list each piece will inherit and each will be filled
    // with legal moves for the specific piece
    public abstract Collection<Move> legalMoves(final Board board);
}