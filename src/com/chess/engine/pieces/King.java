package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class King extends Piece {
    // For an explanation of the King Class
    // please refer to the Knight Class
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

    public King(final int piecePosition, final Alliance pieceAlliance,
                final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
        this.isCastled = false;
    }

    public King(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove,
                final boolean isCastled, final boolean kingSideCastleCapable, final boolean queenSideCastleCapable ) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    @Override
    public int locationBonus() { return this.pieceAlliance.kingBonus(this.piecePosition); }

    public boolean isCastled() { return this.isCastled; }
    public boolean isKingSideCastleCapable() { return this.kingSideCastleCapable; }
    public boolean isQueenSideCastleCapable() { return this.queenSideCastleCapable; }

    @Override
    public Collection<Move> legalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentPossibleMove : POSSIBLE_MOVE_COORDINATES) {
            final int possibleDestinationCoordinate = this.piecePosition + currentPossibleMove;

            if (isFirstColumnExclusion(this.piecePosition, currentPossibleMove)
                || isEightColumnExclusion(this.piecePosition, currentPossibleMove)) {
                continue;
            }

            if (BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)) {
                final Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinate);
                if (!possibleDestinationTile.isTileOccupied()) {
                    legalMoves.add(new NormalMove(board, this, possibleDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if (pieceAlliance != this.pieceAlliance) {
                        legalMoves.add(new MajorAttackMove(board, this,
                                possibleDestinationCoordinate, pieceAtDestination)
                        );
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false,
                move.isCastlingMove(), false , false);
    }

    @Override
    public String toString() { return pieceType.KING.toString(); }

    // King Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition) && (
                possibleOffset == -1
                || possibleOffset == -9
                || possibleOffset == 7
        );
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition) && (
                possibleOffset == 1
                || possibleOffset == 9
                || possibleOffset == -7
        );
    }
}