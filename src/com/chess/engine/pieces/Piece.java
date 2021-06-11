package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {
    protected final PieceType pieceType; // Set method field for piece type
    protected final int piecePosition; // Set int for the position of the piece on the board
    protected final Alliance pieceAlliance; // Set an "alliance" i.e the piece/player color
    protected final boolean isFirstMove; // Set boolean to check for first move
    private final int cachedHashCode; // Set int for the hash code

    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        final int prime = 31;
        int result = pieceType.hashCode();
        result = prime * result + pieceAlliance.hashCode();
        result = prime * result + piecePosition;
        result = prime * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) { return true; }
        if (!(other instanceof Piece)) { return false; }
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition()
                && pieceType == otherPiece.getPieceType()
                && pieceAlliance == otherPiece.getPieceAlliance()
                && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() { return this.cachedHashCode; }
    public int getPiecePosition() { return this.piecePosition; } // Method that returns the piece position
    public Alliance getPieceAlliance() { return this.pieceAlliance; } // Method that returns the pieces color
    public boolean isFirstMove() { return this.isFirstMove; } // Method that returns true if its the first move
    public PieceType getPieceType() { return this.pieceType; } // Method that returns the piece type

    // Abstract list each piece will inherit and each will be filled
    // with legal moves for the specific piece
    public abstract Collection<Move> legalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public enum PieceType {
        PAWN("P") {
            @Override
            public boolean isKing() { return false; }
            @Override
            public boolean isRook() { return false; }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() { return false; }
            @Override
            public boolean isRook() { return false; }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() { return false; }
            @Override
            public boolean isRook() { return false; }
        },
        ROOK("R") {
            @Override
            public boolean isKing() { return false; }
            @Override
            public boolean isRook() { return true; }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() { return false; }
            @Override
            public boolean isRook() { return false; }
        },
        KING("k") {
            @Override
            public boolean isKing() { return true; }
            @Override
            public boolean isRook() { return false; }
        };

        private String pieceName;

        PieceType(final String pieceName) { this.pieceName = pieceName; }

        @Override
        public String toString() { return this.pieceName; }

        public abstract boolean isKing();
        public abstract boolean isRook();
    }
}