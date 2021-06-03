package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import static com.chess.engine.board.Board.*;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destinationCoordinates;

    public static final Move NULL_MOVE = new InvalidMove();

    private Move(final Board board,
                 final Piece movedPiece,
                 final int destinationCoordinates) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinates = destinationCoordinates;
    }

    // Expose method fields
    public int getCurrentCoordinate() { return this.getMovedPiece().getPiecePosition(); }
    public int getDestinationCoordinate() { return this.destinationCoordinates; }
    public Piece getMovedPiece() { return this.movedPiece; }

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

    // Class for handling legal moves
    public static final class NormalMove extends Move {
        public NormalMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }
    }

    // Class for handling attacking
    public static class AttackMove extends Move {
        final Piece attackedPiece;

        public AttackMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinates,
                          final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinates);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() { return null; }
    }

    // TODO
    // Class for moving the pawn
    public static final class PawnMove extends Move {
        public PawnMove(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }
    }

    // TODO
    // Class for handling an attack move with the pawn
    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinates,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinates, attackedPiece);
        }
    }

    // TODO
    // Class for handling the "En Passant" rule/move
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(final Board board,
                                       final Piece movedPiece,
                                       final int destinationCoordinates,
                                       final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinates, attackedPiece);
        }
    }

    // TODO
    // Class for handling the pawn jump
    public static final class PawnJump extends Move {
        public PawnJump(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }
    }

    // TODO
    // Class for handling castle moves
    static abstract class CastleMove extends Move {
        public CastleMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }
    }

    // TODO

    public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }
    }

    // TODO

    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(final Board board,
                                   final Piece movedPiece,
                                   final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }
    }

    // Class for handling invalid moves
    public static final class InvalidMove extends Move {
        public InvalidMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute an invalid move!");
        }
    }

    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("Not Instantiable!");
        }

        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate
                    && move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }

            return NULL_MOVE;
        }
    }
}