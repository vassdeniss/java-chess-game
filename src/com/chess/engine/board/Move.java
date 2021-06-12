package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import static com.chess.engine.board.Board.Builder;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinates;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new InvalidMove();

    private Move(final Board board,
                 final Piece pieceMoved,
                 final int destinationCoordinates) {
        this.board = board;
        this.movedPiece = pieceMoved;
        this.destinationCoordinates = destinationCoordinates;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Board board,
                 final int destinationCoordinates) {
        this.board = board;
        this.destinationCoordinates = destinationCoordinates;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinates;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        result = result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) { return true; }
        if (!(other instanceof Move)) { return false; }
        final Move otherMove = (Move) other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate()
                && getDestinationCoordinate() == otherMove.getDestinationCoordinate()
                && getMovedPiece() == otherMove.getMovedPiece();
    }

    // Expose method fields
    public int getCurrentCoordinate() { return this.getMovedPiece().getPiecePosition(); }
    public int getDestinationCoordinate() { return this.destinationCoordinates; }
    public Piece getMovedPiece() { return this.movedPiece; }

    // Helper methods
    public boolean isAttack() { return false; }
    public boolean isCastlingMove() { return false; }
    public Piece getAttackedPiece() { return null; }

    public Board execute() {
        final Builder builder = new Builder();

        // Loop through the current player pieces
        // if the current piece is not the moved piece
        // just set it on the new board
        for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
            if (!this.movedPiece.equals(piece)) {
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

    // Class for handling attacks
    public static class MajorAttackMove extends AttackMove {
        public MajorAttackMove(final Board board, final Piece movedPiece,
                               final int destination, final Piece attackedPiece) {
            super(board, movedPiece, destination, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() + BoardUtils.getPostionAtCoordinate(this.destinationCoordinates);
        }
    }

    // Class for handling legal moves
    public static final class NormalMove extends Move {
        public NormalMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof NormalMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + BoardUtils.getPostionAtCoordinate(this.destinationCoordinates);
        }
    }

    // Class for handling attacks (middleman)
    public static class AttackMove extends Move {
        private final Piece attackedPiece;

        public AttackMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinates,
                          final Piece pieceAttacked) {
            super(board, movedPiece, destinationCoordinates);
            this.attackedPiece = pieceAttacked;
        }

        @Override
        public int hashCode() { return this.attackedPiece.hashCode() + super.hashCode(); }

        @Override
        public boolean equals(final Object other) {
            if (this == other) { return true; }
            if (!(other instanceof AttackMove)) { return false; }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove)
                    && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack() { return true; }

        @Override
        public Piece getAttackedPiece() { return this.attackedPiece; }
    }

    // Class for moving the pawn
    public static final class PawnMove extends Move {
        public PawnMove(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString() { return BoardUtils.getPostionAtCoordinate(this.destinationCoordinates); }
    }

    // Class for handling an attack move with the pawn
    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinates,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinates, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        // PGN format
        @Override
        public String toString() {
            return BoardUtils.getPostionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) + "x" +
                    BoardUtils.getPostionAtCoordinate(this.destinationCoordinates);
        }
    }

    // Class for handling the "En Passant" rule/move
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(final Board board,
                                       final Piece movedPiece,
                                       final int destinationCoordinates,
                                       final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinates, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                if (!piece.equals(this.getAttackedPiece())) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveDecider(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    // Class for handling the pawn jump
    public static final class PawnJump extends Move {
        public PawnJump(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinates) {
            super(board, movedPiece, destinationCoordinates);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();

            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            // Record the pawn that just jumped to be
            // an en passant pawn for the next board
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveDecider(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() { return BoardUtils.getPostionAtCoordinate(this.destinationCoordinates); }
    }

    // Class for handling castle moves
    static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookPosition;
        protected final int castleRookDestination;

        public CastleMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinates,
                          final Rook castleRook,
                          final int castleRookPosition,
                          final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinates);
            this.castleRook = castleRook;
            this.castleRookPosition = castleRookPosition;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() { return this.castleRook; }

        @Override
        public boolean isCastlingMove() { return true; }

        @Override
        public Board execute() {
            final Builder builder = new Builder();

            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance(), false));
            builder.setMoveDecider(this.board.currentPlayer().getOpponent().getAlliance());

            return builder.build();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) { return true; }
            if (!(other instanceof CastleMove)) { return false; }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    // Class for handling castle moves with king
    public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinates,
                                  final Rook castleRook,
                                  final int castleRookPosition,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinates, castleRook, castleRookPosition, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() { return "O-O"; } // Portable Game Notation Standard
    }

    // Class for handling castle moves with queen
    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(final Board board,
                                   final Piece movedPiece,
                                   final int destinationCoordinates,
                                   final Rook castleRook,
                                   final int castleRookPosition,
                                   final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinates, castleRook, castleRookPosition, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() { return "O-O-O"; } // Portable Game Notation Standard
    }

    // Class for handling invalid moves
    public static final class InvalidMove extends Move {
        public InvalidMove() { super(null, 65); }

        @Override
        public Board execute() { throw new RuntimeException("Cannot execute an invalid move!"); }

        @Override
        public int getCurrentCoordinate() { return -1; }
    }

    public static class MoveFactory {
        private MoveFactory() { throw new RuntimeException("Not Instantiable!"); }

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