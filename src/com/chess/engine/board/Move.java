package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import static com.chess.engine.board.Board.*;

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

    // Expose method fields
    public int getDestinationCoordinate() { return this.destinationCoordinates; }
    public Piece getMovedPiece() { return this.movedPiece; }

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
            final Builder builder = new Builder();

            // Loop through the current player pieces
            // if the current piece is not the moved piece
            // just set it on the new board
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                // TODO hashcode / equals for pieces
                if (this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            // Loop through the opponent pieces and sets the pieces
            // because the opponent doesn't have moved pieces
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveDecider(this.board.currentPlayer().getOpponent().getAlliance());

            return builder.build();
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