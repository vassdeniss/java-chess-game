package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destinationCoordinates;

    private Move(final Board board,
                final Piece movedPiece,
                final int destinationCoordinates) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinates = destinationCoordinates;
    }

    // Expose destination coordinates
    public int getDestinationCoordinate() { return this.destinationCoordinates; }

    public abstract Board execute();

    // NormalMove class for legal moves that just moves the piece
    public static final class NormalMove extends Move {
        public NormalMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    // AttackMove class for legal moves and attacking the piece
    // if the piece is black
    public static final class AttackMove extends Move {
        final Piece attackedPiece;

        public AttackMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinates,
                          final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinates);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }
    }
}