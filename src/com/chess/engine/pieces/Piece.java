package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {
    // Set int for the position of the piece on the board
    // Set an "alliance" i.e the piece/player color
    protected final int piecePosition;
    protected final Alliance pieceAlliance;

    // Constructor
    Piece(final int piecePosition, final Alliance pieceAlliance) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
    }

    // Method for taking the pieces color
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    // Abstract list each piece will inherit and each will be filled
    // with legal moves for the specific piece
    public abstract Collection<Move> legalMoves(final Board board);
}